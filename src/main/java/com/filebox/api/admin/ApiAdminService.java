package com.filebox.api.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.File;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.Postage;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * @Description:TODO(管理员api业务逻辑层)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月21日
 */
public class ApiAdminService {
	public static final ApiAdminService me = new ApiAdminService();
	static final File fileDao = new File().dao();
	static final FileBox fileBoxDao = new FileBox().dao();

	public Record getDeviceInfo(String deviceId) {
		return Db.findFirst("select * from file_device where id = ? ", deviceId);
	}

	public RetKit getFileBoxOwnerList(String deviceId, int pageSize, int pageNum) {
		Page<Record> list = Db.paginate(pageNum, pageSize, "select a.*",
				"from file_box_owner a , owner_device b where a.id = b.owner_id and b.device_id = ?", deviceId);
		list.getList().remove("password");
		list.getList().remove("password_card");
		list.getList().remove("password_fingerprint");
		return RetKit.ok("data", list);
	}

	public RetKit searchOwner(String deviceId, String searchValue, int pageSize, int pageNum) {
		Page<Record> list = Db.paginate(pageNum, pageSize, "a.*",
				"from file_box_owner a, owner_device b " + "where a.id = b.owner_id and b.device_id = ?"
						+ "and (a.phone like '%" + searchValue + "%' or a.id = ?)",
				deviceId, searchValue);
		list.getList().remove("password");
		list.getList().remove("password_card");
		list.getList().remove("password_fingerprint");
		return RetKit.ok("data", list);

	}

	public Page<Record> getDeliveryList(String deviceId, String searchValue, int pageSize, int pageNum) {
		String searchSql = "";
		if (StrKit.notBlank(searchValue)) {
			searchSql = " and (a.courier_phone like '%" + searchValue + "%' " + "or a.receive_phone like '%"
					+ searchValue + "%'";
		}
		Page<Record> list = Db.paginate(pageNum, pageSize,
				"select a.*,b.file_id,b.pickup_phone,b.pickup_time,b.pickup_identity,b.cost",
				"from file a left join pickup_record b on a.id = b.file_id where a.device_id = ? " + searchSql
						+ " order by a.cabinet_num,a.box_number",
				deviceId);
		return list;
	}

	public RetKit addNewDevice(final List<Device> list, final String deviceId) {
		Record deviceRecord = Db.findFirst("select * from file_device where id = ? ", deviceId);
		if (deviceRecord != null) {
			// return RetKit.fail("设备ID已存在");
			return updateDevice(list, deviceId);
		}

		boolean succ = Db.tx(new IAtom() {

			public boolean run() throws SQLException {
				FileDevice fileDevice = new FileDevice();
				fileDevice.setId(deviceId);
				fileDevice.setStatus(FileDevice.STATUS_DISABLE);
				fileDevice.setSysupdate(new Date());
				fileDevice.setCabinetAmount(new Long(list.size()));
				// 保存设备
				boolean succ1 = fileDevice.update();
				// 初始化设备资费标准
				boolean succ2 = createPostAge(deviceId);
				for (Device device : list) {
					int doorStatus[] = device.getDoorStatus();
					for (int i = 0; i < doorStatus.length; i++) {
						FileBox box = new FileBox();
						box.setCabinetNum(device.getCabinet_num());
						box.setDeviceId(deviceId);
						box.setDoorStatus(doorStatus[i]);
						box.setNumber(i + 1);
						// 获取箱子规格
						Record boxRecord = getBoxSpec(device.getCabinetModel(), i + 1);
						if (boxRecord == null) {
							return false;
						}
						box.setSpec(boxRecord.getInt("box_spec"));
						box.setFloor(boxRecord.getInt("box_floor"));
						box.save(); // 保存箱子
					}
				}
				return succ1 && succ2;
			}
		});

		return succ ? RetKit.ok() : RetKit.fail("型号不存在");
	}

	private RetKit updateDevice(final List<Device> list, final String deviceId) {
		boolean succ = Db.tx(new IAtom() {

			public boolean run() throws SQLException {
				// cabinetList 是柜体的数量
				List<FileBox> canbinetList = fileBoxDao
						.find("select cabinet_num from file_box where device_id = ? group by cabinet_num", deviceId);
				FileDevice fileDevice = new FileDevice();
				fileDevice.setId(deviceId);
				fileDevice.setSysupdate(new Date());
				fileDevice.setCabinetAmount(new Long(list.size()));
				fileDevice.update(); // 保存设备
				List<FileBox> boxList = new ArrayList<FileBox>();
				for (Device device : list) {
					boolean isExistCabinet = false;
					if (canbinetList != null) {
						// 判断是否有删除的快递柜
						// 如果有 则删除该号快递柜
						int cabinetNum = 0;
						for (FileBox cabinet : canbinetList) {
							cabinetNum = cabinet.getCabinetNum();
							if (cabinet.getCabinetNum() == device.getCabinet_num()) {
								isExistCabinet = true;
								continue;
							}
						}
						// 如果不存在该快递柜 则删除
						if (!isExistCabinet) {
							Db.update("delete from file_box where device_id = ? and cabinet_num = ?", deviceId,
									cabinetNum);
						}
					}

					int doorStatus[] = device.getDoorStatus();
					FileBox boxModel = fileBoxDao.findFirst(
							"select * from file_box " + "where device_id = ? and cabinet_num = ? limit 1", deviceId,
							device.getCabinet_num());
					// 如果找不到此文件柜则新增
					if (boxModel == null) {
						if(doorStatus == null){
							RetKit.fail("更新失败");
						}
						for (int i = 0; i < doorStatus.length; i++) {
							FileBox box = new FileBox();
							box.setCabinetNum(device.getCabinet_num());
							box.setDeviceId(deviceId);
							box.setDoorStatus(doorStatus[i]);
							box.setNumber(i + 1);
							// 获取箱子规格
							Record boxRecord = getBoxSpec(device.getCabinetModel(), i + 1);
							if (boxRecord == null) {
								return false;
							}
							box.setSpec(boxRecord.getInt("box_spec"));
							box.setFloor(boxRecord.getInt("box_floor"));
							boxList.add(box);
							// box.save();
						}
					} else {
						// 判断箱子个数是否一致，如果数据库箱子比提交的箱子数多，则删除数据路的箱子
						List<FileBox> tempBoxList = fileBoxDao.find(
								"select * from file_box " + "where device_id = ? and cabinet_num = ?", deviceId,
								device.getCabinet_num());
						if (tempBoxList.size() > doorStatus.length) {
							for (int i = tempBoxList.size(); i < doorStatus.length; i++) {
								Db.update(
										"delete from file_box where"
												+ " device_id = ? and cabinet_num = ? and number = ?",
										deviceId, device.getCabinet_num(), i + 1);
							}
						}
						// 更新箱子状态
						for (int i = 0; i < doorStatus.length; i++) {
							// 判断是否有该号箱子
							Record boxRecord = getBoxSpec(device.getCabinetModel(), i + 1);
							if (boxRecord == null) {
								continue;
							}
							int count = Db.update(
									"update file_box set door_status = ?,floor = ?,spec = ? "
											+ "where device_id = ? and cabinet_num = ? and number = ?",
									doorStatus[i], boxRecord.getInt("box_floor"), boxRecord.getInt("box_spec"),
									deviceId, device.getCabinet_num(), i + 1);
							// 如果count 为 0，则说明有新增的箱子，则添加。
							if (count == 0) {
								FileBox box = new FileBox();
								box.setCabinetNum(device.getCabinet_num());
								box.setDeviceId(deviceId);
								box.setDoorStatus(doorStatus[i]);
								box.setNumber(i + 1);
								// 获取箱子规格
								box.setSpec(boxRecord.getInt("box_spec"));
								box.setFloor(boxRecord.getInt("box_floor"));
								boxList.add(box);
							}
						}
					}
				}
				if (boxList.size() > 0) {
					Db.batchSave(boxList, boxList.size());// 保存箱子
				}
				return true;
			}
		});

		return succ ? RetKit.ok() : RetKit.fail("更新失败");
	}
	

	/***
	 * 初始化设备资费
	 */
	public boolean createPostAge(String deviceId) {
		Db.update("delete from postage where device_id = ?", deviceId);
		boolean succ1 = new Postage().setDeviceId(deviceId).setType(Postage.TYPE_SM).save();
		boolean succ2 = new Postage().setDeviceId(deviceId).setType(Postage.TYPE_MD).save();
		boolean succ3 = new Postage().setDeviceId(deviceId).setType(Postage.TYPE_LG).save();

		return succ1 && succ2 && succ3;
	}

	private Record getBoxSpec(String code, int boxNum) {
		Record record = Db.findFirst(
				"select * from "
						+ "model a,model_detail b  where a.code = ? and a.id = b.model_id and b.box_num = ? limit 1",
				code, boxNum);
		return record;
	}

}
