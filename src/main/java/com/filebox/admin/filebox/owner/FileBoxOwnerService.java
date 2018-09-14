package com.filebox.admin.filebox.owner;

import java.rmi.server.UID;
import java.util.Date;
import java.util.List;

import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.filebox.admin.filebox.FileBoxService;
import com.filebox.api.common.CommonService;
import com.filebox.api.owner.OwnerDeviceTemp;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RandomKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileBoxOwner;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.Operator;
import com.filebox.common.model.OwnerDetail;
import com.filebox.common.model.OwnerDevice;
import com.filebox.utils.CytSdkUtil;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.Random;

import sun.security.krb5.internal.ccache.CCacheInputStream;

/**
 * @Description:TODO(信报箱归属人业务逻辑)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月26日
 */
public class FileBoxOwnerService {
	public static final FileBoxOwnerService me = new FileBoxOwnerService();
	public static final FileBoxOwner fileBoxOwnerDao = new FileBoxOwner();
	public static final OwnerDetail ownerDetailDao = new OwnerDetail();
	public static final OwnerDevice ownerDeviceDao = new OwnerDevice();

	/**
	 * 箱子归属人信息
	 */
	public FileBoxOwner fileBoxOwnerInfo(int ownerId) {
		return fileBoxOwnerDao.findFirst("select a.*,b.unit,b.floor,b.room_address,b.owner_id,b.device_id,b.password_card,b.password_fingerprint "
				+ "from file_box_owner a left join owner_detail b on a.id = b.owner_id "
				+ "where a.id = ?",ownerId);
	}

	/**
	 * 箱子归属人信息
	 */
	public FileBoxOwner fileBoxOwnerInfo(String phone) {
		return fileBoxOwnerDao.findFirst("select a.*,b.unit,b.floor,b.room_address,b.owner_id,b.device_id,b.password_card,b.password_fingerprint "
				+ "from file_box_owner a left join owner_detail b on a.id = b.owner_id "
				+ "where a.phone = ?",phone);
	}

	/**
	 * 箱子归属人信息详情
	 */
	public Record fileBoxOwnerInfo(int ownerId, String deviceId) {
		return Db.findFirst(
				"select b.*,c.unit,c.floor,c.room_address,c.owner_id,c.device_id,c.password_card,c.password_fingerprint "
				+ "from (select * from file_box_owner a where a.id = ?) b"
						+ " left join owner_detail c on b.id = c.owner_id  and c.device_id = ?",
				ownerId, deviceId);
	}

	/**
	 * 获取指定设备 用户和箱子的绑定结果
	 * 
	 * @param deviceId
	 * @return
	 */
	public List<Record> boxOwnerList(String deviceId) {
		return Db.find(
				"select a.*,b.owner_id,b.device_id,b.cabinet_num,b.number,c.unit,c.floor,c.room_address,c.password_card,c.password_fingerprint,c.owner_device_status, count(a.id) as device_count "
						+ "b.owner_id = a.id and b.device_id = ? " + "left join owner_detail c on c.owner_id = a.id and c.device_id = b.device_id ",
				deviceId);
	}

	/**
	 * 获取指定设备 用户和箱子的绑定结果（一个用户只显示一条）
	 * 
	 * @param deviceId
	 * @return
	 */
	public List<Record> boxOwnerListByGroup(String deviceId) {
		return Db
				.find("select a.*,b.owner_id,b.device_id,b.cabinet_num,b.number,c.unit,c.floor,c.room_address,c.password_card,c.password_fingerprint,c.owner_device_status, count(a.id) as device_count "
						+ "from file_box_owner a inner join owner_device b on "
						+ "b.owner_id = a.id and b.device_id = ? " + "left join owner_detail c on c.owner_id = a.id and c.device_id = b.device_id "
						+ "group by b.owner_id", deviceId);
	}

	/**
	 * 用户信息和绑定该信报箱箱子信息
	 */
	public Record ownerWithBoxsInfo(String deviceId, String ownerId) {
		return Db.findFirst("select * from owner_device a join file_box_owner b on a.owner_id = b.id and "
				+ "a.device_id = ?  and owner_id = ?", deviceId, ownerId);
	}

	/**
	 * 指定用户所拥有的该设备箱子列表
	 */
	public List<Record> ownerDeviceList(String ownerId, String deviceId) {
		return Db.find("select * from owner_device a where a.device_id = ? and a.owner_id = ?", deviceId, ownerId);
	}

	/**
	 * 指定用户所拥有的设备箱
	 */
	public List<Record> ownerDevice(int ownerId) {
		return Db.find("select * from owner_device", ownerId);
	}
	
	/**
	 * 
	 * 创建新的箱子归属人
	 */
	public RetKit addBoxOwner(String phone, String unit, String floor, String roomAddress, String deviceId,
			String passwordCard,String passwordFingerPrint, int cabinet_num, int number) {
		Operator operator = CommonService.me.getOperator(deviceId);
		if (operator == null) {
			return RetKit.fail("请先设置运营商");
		}

		FileBoxOwner owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where phone = ?", phone);
		boolean succ = false;
		if (owner == null) {
			succ = createBoxOwner(phone, unit, floor, roomAddress, deviceId, passwordCard,passwordFingerPrint, cabinet_num, number);
		} else {
			FileBoxOwner owner2 = fileBoxOwnerDao.findFirst(
					"select a.* from file_box_owner a , owner_device b where a.id = b.owner_id"
							+ " and a.phone = ? and b.device_id = ? and b.cabinet_num = ? and b.number = ?",
					phone, deviceId, cabinet_num, number);
			if (owner2 != null) {
				OwnerDetail ownerDetail = ownerDetailDao.findFirst("select * from owner_detail where owner_id = ?",
						owner2.getId());
				if (ownerDetail != null) { // 详情没有设置
					ownerDetail.setUnit(unit);
					ownerDetail.setFloor(floor);
					ownerDetail.setRoomAddress(roomAddress);
					ownerDetail.setOwnerId(owner2.getId());
					ownerDetail.setPasswordCard(passwordCard);
					ownerDetail.setPasswordFingerprint(passwordFingerPrint);
					ownerDetail.setDeviceId(deviceId);
					ownerDetail.setSysupdate(new Date());
					ownerDetail.update();
				} else {
					ownerDetail = new OwnerDetail();
					ownerDetail.setUnit(unit);
					ownerDetail.setFloor(floor);
					ownerDetail.setRoomAddress(roomAddress);
					ownerDetail.setOwnerId(owner2.getId());
					ownerDetail.setDeviceId(deviceId);
					ownerDetail.setPasswordCard(passwordCard);
					ownerDetail.setPasswordFingerprint(passwordFingerPrint);
					ownerDetail.setSysupdate(new Date());
					ownerDetail.save();

				}

				OwnerDevice ownerDevice = ownerDeviceDao.findFirst(
						"select a.* from file_box_owner a , owner_device b where a.id = b.owner_id"
								+ " and a.phone = ? and b.device_id = ? and b.cabinet_num = ? and b.number = ?",
						phone, deviceId, cabinet_num, number);
				ownerDevice.setDeviceId(deviceId);
				ownerDevice.setCabinetNum(cabinet_num);
				ownerDevice.setNumber(number);
				ownerDevice.update();

				return RetKit.ok("归属人信息修改成功");
				// return RetKit.fail(FileConstant.code_801,"手机号已存在");
			} else {
				succ = createBoxOwner(owner, phone, deviceId, cabinet_num, number);
			}
		}

		return succ ? RetKit.ok() : RetKit.fail("新增归属人失败，请联系系统管理员");
	}

	/**
	 * 创建新的箱子归属人 number 是箱子编号
	 */
	public boolean createBoxOwner(String phone, String unit, String floor, String roomAddress, String deviceId,
			String passwordCard,String passwordFingerPrint, int cabinet_num, int number) {
		FileBoxOwner fileBoxOwner = new FileBoxOwner();
		fileBoxOwner.setPhone(phone);
		fileBoxOwner.setSysupdate(new Date());
		String password = RandomKit.getRandomPsw(6);
		fileBoxOwner.setPassword(HashKit.md5(password));

		boolean succ1 = fileBoxOwner.save();
		if (succ1) {
			FileBoxOwner owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where phone = ?", phone);
			if (owner != null) {
				OwnerDetail ownerDetail = new OwnerDetail();
				ownerDetail.setUnit(unit);
				ownerDetail.setFloor(floor);
				ownerDetail.setRoomAddress(roomAddress);
				ownerDetail.setOwnerId(owner.getId());
				ownerDetail.setDeviceId(deviceId);
				ownerDetail.setPasswordCard(passwordCard);
				ownerDetail.setPasswordFingerprint(passwordFingerPrint);
				ownerDetail.setSysupdate(new Date());
				ownerDetail.save();
			}

			FileDevice fileDevice = CommonService.me.getDeviceInfo(deviceId);
			String msg = "您已获取“" + fileDevice.getName() + "”使用权，信报箱地址：" + fileDevice.getAddress() + "" + "。 初始登录密码为："
					+ password;
			// CytSdkUtil.sendMessage(phone, msg);
		}

		OwnerDevice odDevice = new OwnerDevice();
		odDevice.setOwnerId(fileBoxOwner.getId());
		odDevice.setNumber(number);
		odDevice.setCabinetNum(cabinet_num);
		odDevice.setDeviceId(deviceId);
		boolean succ2 = odDevice.save(); // 保存箱子归属人-信报箱
		return succ1 && succ2;
	}

	/**
	 * 归属人已经存在，只做关联信报箱操作 number 是箱子编号
	 */
	public boolean createBoxOwner(FileBoxOwner fileBoxOwner, String phone, String deviceId, int cabinet_num,
			int number) {
		OwnerDevice odDevice = new OwnerDevice();
		odDevice.setOwnerId(Integer.valueOf(fileBoxOwner.getId().toString()));
		odDevice.setCabinetNum(cabinet_num);
		odDevice.setNumber(number);
		odDevice.setDeviceId(deviceId);
		boolean succ = odDevice.save(); // 保存箱子归属人-信报箱
		if (succ) {
			FileDevice fileDevice = CommonService.me.getDeviceInfo(deviceId);
			String msg = "您已经被添加到" + fileDevice.getName() + ",信报箱地址：" + fileDevice.getAddress();
			// CytSdkUtil.sendMessage(phone, msg);
		}
		return succ;
	}

	/**
	 * 归属人已经存在，只做关联信报箱操作多个箱子 number 是箱子编号
	 */
	public boolean createBoxOwner(FileBoxOwner fileBoxOwner, String deviceId, List<OwnerDeviceTemp> bindBoxList) {
		boolean succ = true;
		if (bindBoxList != null && bindBoxList.size() > 0) {
			int succedCount = 0; // 记录保存成功数量
			for (int i = 0; i < bindBoxList.size(); i++) {
				OwnerDevice odDevice = new OwnerDevice();
				odDevice.setOwnerId(Integer.valueOf(fileBoxOwner.getId().toString()));
				odDevice.setCabinetNum(bindBoxList.get(i).getCabinet_num());
				odDevice.setNumber(bindBoxList.get(i).getNumber());
				odDevice.setDeviceId(deviceId);
				boolean succ1 = odDevice.save(); // 保存箱子归属人-信报箱
				if (succ1) {
					succedCount++;
				}
			}

			if (succedCount == bindBoxList.size()) {
				succ = true;
				FileDevice fileDevice = CommonService.me.getDeviceInfo(deviceId);
				String msg = "您已经被添加到" + fileDevice.getName() + ",信报箱地址：" + fileDevice.getAddress();
				// CytSdkUtil.sendMessage(phone, msg);
			} else {
				succ = false;
			}
		}
		return succ;
	}

	/**
	 * 
	 * 新增或修改用户绑定N个箱子
	 */

	public RetKit addOrModifyBoxOwner(String phone, String unit, String floor, String roomAddress, String deviceId,
			String passwordCard,String passwordFingerPrint, List<OwnerDeviceTemp> bindBoxList) {
		Operator operator = CommonService.me.getOperator(deviceId);
		if (operator == null) {
			return RetKit.fail("请先设置运营商");
		}

		FileBoxOwner owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where phone = ?", phone);
		boolean succ = false;
		if (owner == null) {
			succ = createBoxOwner(phone, unit, floor, roomAddress, deviceId, passwordCard,passwordFingerPrint, bindBoxList);
		} else {
			OwnerDetail ownerDetail = ownerDetailDao.findFirst("select * from owner_detail where owner_id = ? and device_id = ?",
					owner.getId(),deviceId);
			if (ownerDetail != null) { // 详情没有设置
				ownerDetail.setUnit(unit);
				ownerDetail.setFloor(floor);
				ownerDetail.setRoomAddress(roomAddress);
				ownerDetail.setOwnerId(owner.getId());
				ownerDetail.setPasswordCard(passwordCard);
				ownerDetail.setPasswordFingerprint(passwordFingerPrint);
				ownerDetail.setDeviceId(deviceId);
				ownerDetail.setSysupdate(new Date());
				ownerDetail.update();
			} else {
				ownerDetail = new OwnerDetail();
				ownerDetail.setUnit(unit);
				ownerDetail.setFloor(floor);
				ownerDetail.setRoomAddress(roomAddress);
				ownerDetail.setOwnerId(owner.getId());
				ownerDetail.setDeviceId(deviceId);
				ownerDetail.setPasswordCard(passwordCard);
				ownerDetail.setPasswordFingerprint(passwordFingerPrint);
				ownerDetail.setSysupdate(new Date());
				ownerDetail.save();

			}
			// 将该用户对应deviceId的绑定全部删除
			Db.update("delete from owner_device where device_id = ? and owner_id = ?", deviceId, owner.getId());

			succ = createBoxOwner(owner, deviceId, bindBoxList);

		}

		return succ ? RetKit.ok() : RetKit.fail("修改归属人信息失败，请联系系统管理员");
	}

	/**
	 * 创建新的箱子归属人
	 */
	public boolean createBoxOwner(String phone, String unit, String floor, String roomAddress, String deviceId,
			String passwordCard,String passwordFingerPrint, List<OwnerDeviceTemp> bindBoxList) {
		FileBoxOwner fileBoxOwner = new FileBoxOwner();
		fileBoxOwner.setPhone(phone);
		fileBoxOwner.setSysupdate(new Date());
		String password = RandomKit.getRandomPsw(6);
		fileBoxOwner.setPassword(HashKit.md5(password));

		boolean succ1 = fileBoxOwner.save();
		if (succ1) {
			FileBoxOwner owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where phone = ?", phone);
			if (owner != null) {
				OwnerDetail ownerDetail = new OwnerDetail();
				ownerDetail.setUnit(unit);
				ownerDetail.setFloor(floor);
				ownerDetail.setRoomAddress(roomAddress);
				ownerDetail.setOwnerId(owner.getId());
				ownerDetail.setDeviceId(deviceId);
				ownerDetail.setPasswordCard(passwordCard);
				ownerDetail.setPasswordFingerprint(passwordFingerPrint);
				ownerDetail.setSysupdate(new Date());
				ownerDetail.save();
			}
			FileDevice fileDevice = CommonService.me.getDeviceInfo(deviceId);
			String msg = "您已获取“" + fileDevice.getName() + "”使用权，信报箱地址：" + fileDevice.getAddress() + "" + "。 初始登录密码为："
					+ password;
			// CytSdkUtil.sendMessage(phone, msg);
		}

		boolean succ2 = true;
		if (bindBoxList != null && bindBoxList.size() > 0) {
			int ownerDeviceSuccCount = 0; // 保存成功的数量
			for (OwnerDeviceTemp iTemp : bindBoxList) {
				OwnerDevice odDevice = new OwnerDevice();
				odDevice.setOwnerId(fileBoxOwner.getId());
				odDevice.setCabinetNum(iTemp.getCabinet_num());
				odDevice.setNumber(iTemp.getNumber());
				odDevice.setDeviceId(deviceId);
				// 保存箱子归属人-信报箱
				if (odDevice.save()) {
					ownerDeviceSuccCount++;
				}
			}
			if (ownerDeviceSuccCount != bindBoxList.size()) {
				succ2 = false;
			}
		}

		return succ1 && succ2;
	}

	/**
	 * 更新用户状态
	 */
	public RetKit updateOwnerStatus(int ownerId, int status) {
		FileBoxOwner fileBoxOwner = fileBoxOwnerDao.findFirst("select * from file_box_owner where id = ?", ownerId);
		fileBoxOwner.setStatus(status);
		boolean succ = fileBoxOwner.update();

		return succ ? RetKit.ok() : RetKit.fail("更改失败");
	}
	
	/**
	 * 更新用户指定设备状态
	 */
	public RetKit updateOwnerDeviceStatus(int ownerId,String deviceId, int status) {
		OwnerDetail ownerDetail = ownerDetailDao.findFirst("select * from owner_detail where owner_id = ? and device_id = ?", ownerId,deviceId);
		ownerDetail.setOwnerDeviceStatus(status);
		boolean succ = ownerDetail.update();

		return succ ? RetKit.ok() : RetKit.fail("更改失败");
	}

	/**
	 * 删除归属人
	 */
	public RetKit deleteOwner(int ownerId) {
		int n = Db.update("delete from file_box_owner where id = ?", ownerId);
		if (n > 0) {
			return RetKit.ok("msg", "删除成功");
		} else {
			return RetKit.fail("msg", "删除失败");
		}
	}
	/**
	 * 删除归属人绑定的某个箱子
	 */
	public RetKit unBindBox(int ownerId,String deviceId,int cabinetNum,int number){
		int n = Db.update("delete from owner_device where owner_id = ? and device_id = ? and cabinet_num = ? and number = ?"
				,ownerId,deviceId,cabinetNum,number);
		if(n > 0){
			return RetKit.ok("msg", "解绑成功");
		}else{
			return RetKit.fail("msg", "解绑失败");
		}
	}

	/**
	 * 查看指定设备用户绑定的设备列表
	 */
	public List<OwnerDevice> getOwnerBindBoxsList(int ownerId, String deviceId) {
		return ownerDeviceDao.find("select * from owner_device where owner_id = ? and device_id = ?", ownerId,
				deviceId);
	}
	
	/**
	 * 批量导入用户
	 */
	
	
}
