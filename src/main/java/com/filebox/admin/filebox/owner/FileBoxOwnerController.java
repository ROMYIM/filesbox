package com.filebox.admin.filebox.owner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSON;
import com.filebox.admin.device.OwnerDeviceService;
import com.filebox.admin.filebox.FileBoxService;
import com.filebox.api.admin.ApiAdminController;
import com.filebox.api.owner.OwnerDeviceTemp;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.inteceptor.DeviceInterceptor;
import com.filebox.common.kit.ExcelKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileBoxOwner;
import com.filebox.common.model.OwnerDetail;
import com.filebox.common.model.OwnerDevice;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.Str;
import com.jfinal.upload.UploadFile;
import com.sun.swing.internal.plaf.metal.resources.metal;

import me.hao0.common.util.Strings;
import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @Description:TODO()
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月17日
 */
@Before(DeviceInterceptor.class)
public class FileBoxOwnerController extends Controller {

	public static final FileBoxOwnerService service = FileBoxOwnerService.me;

	public void index() {
		String deviceId = getAttr(FileConstant.deviceId);
		setAttr("list", service.boxOwnerListByGroup(deviceId));
		render("index.html");
	}

	/**
	 * 禁用归属人
	 */
	public void lockOwner() {
		int ownerId = getParaToInt();
		RetKit retKit = service.updateOwnerStatus(ownerId, FileBoxOwner.STATUS_LOCK);
		renderJson(retKit);
	}

	/**
	 * 启用归属人
	 */
	public void unlockOwner() {
		int ownerId = getParaToInt();
		RetKit retKit = service.updateOwnerStatus(ownerId, FileBoxOwner.STATUS_OK);
		renderJson(retKit);
	}

	/**
	 * 禁用归属人指定设备
	 */
	public void lockOwnerDevice() {
		String deviceId = getAttr(FileConstant.deviceId);
		int ownerId = getParaToInt();
		RetKit retKit = service.updateOwnerDeviceStatus(ownerId, deviceId, FileBoxOwner.STATUS_LOCK);
		renderJson(retKit);
	}

	/**
	 * 启用归属人指定设备
	 */
	public void unlockOwnerDevice() {
		String deviceId = getAttr(FileConstant.deviceId);
		int ownerId = getParaToInt();
		RetKit retKit = service.updateOwnerDeviceStatus(ownerId, deviceId, FileBoxOwner.STATUS_OK);
		renderJson(retKit);
	}

	/**
	 * 删除归属人
	 */
	public void deleteOwner() {
		int ownerId = getParaToInt();
		RetKit retKit = service.deleteOwner(ownerId);
		renderJson(retKit);
	}

	/**
	 * 删除指定设备归属人
	 */
	public void deleteOwnerDevice() {
		String deviceId = getAttr(FileConstant.deviceId);
		int ownerId = getParaToInt();
		RetKit retKit = OwnerDeviceService.me.deleteOwnerDevice(ownerId, deviceId);
		renderJson(retKit);
	}

	/**
	 * 添加归属人
	 */
	public void add() {
		String deviceId = getAttr(FileConstant.deviceId);
		int ownerId = 0;
		setAttr("owner", service.fileBoxOwnerInfo(ownerId, deviceId));
		List<Record> list = FileBoxService.me.boxListNotUseInfoOrIncludeSepcialOwner(deviceId, ownerId);
		setAttr("boxslist", list);
		render("form.html");
	}

	/**
	 * 编辑归属人(归属人信息和箱子列表（未使用和指定owner_id的)
	 */
	public void update() {
		String deviceId = getAttr(FileConstant.deviceId);
		int ownerId = getParaToInt();
		setAttr("owner", service.fileBoxOwnerInfo(ownerId, deviceId));
		Record record = service.fileBoxOwnerInfo(ownerId, deviceId);
		List<Record> list = FileBoxService.me.boxListNotUseInfoOrIncludeSepcialOwner(deviceId, ownerId);
		setAttr("boxslist", list);
		render("form.html");
	}

	/**
	 * 增加和修改箱子绑定人(一个人绑定多个箱子)
	 */
	@Before(AddOrModifyBoxOwnerValidator.class)
	public void addOrUpdateOwnerWithBoxs() {
		String deviceId = getAttr(FileConstant.deviceId);
		String phone = getPara("phone");
		String unit = getPara("unit");
		String floor = getPara("floor");
		String roomAddress = getPara("roomAddress");
		String passwordCard = getPara("passwordCard");
		String passwordFingerPrint = getPara("passwordFingerPrint");
		String selectedBoxIds = getPara("ids");
		List<OwnerDeviceTemp> bindBoxList = new ArrayList<>();
		if (!StrKit.isBlank(selectedBoxIds)) {
			selectedBoxIds = selectedBoxIds.substring(0, selectedBoxIds.length() - 1); // 截取最后一个逗号
			String[] selectedBoxsIdArray = selectedBoxIds.split(",");
			if (selectedBoxsIdArray != null) {
				for (int i = 0; i < selectedBoxsIdArray.length; i++) {
					OwnerDeviceTemp deviceTemp = new OwnerDeviceTemp();
					String[] itemArray = selectedBoxsIdArray[i].split("-");
					if (itemArray != null) {
						deviceTemp.setCabinet_num(Integer.parseInt(itemArray[0]));
						deviceTemp.setNumber(Integer.parseInt(itemArray[1]));
						bindBoxList.add(deviceTemp);
					}
				}
			}

		}
		RetKit retKit = FileBoxOwnerService.me.addOrModifyBoxOwner(phone, unit, floor, roomAddress, deviceId,
				passwordCard, passwordFingerPrint, bindBoxList);
		renderJson(retKit);
	}

	/**
	 * 获取用户指定deviceId的个人信息
	 */
	public void getOwnerInfo() {
		String phone = getPara("myPhone");
		if (phone == null) {
			renderJson(RetKit.ok("data", ""));
		} else {
			FileBoxOwner fileBoxOwner = service.fileBoxOwnerInfo(phone);
			renderJson(fileBoxOwner != null ? RetKit.ok("data", fileBoxOwner) : RetKit.ok("data", ""));
		}
	}

	/**
	 * 解绑具体一个箱子
	 */
	public void unBindBox() {
		String deviceId = getAttr(FileConstant.deviceId);
		int boxId = getParaToInt("id", 0);
		FileBox fileBox = FileBoxService.me.getFileBox(boxId);
		if (fileBox != null) {
			int ownerId = OwnerDeviceService.me.getBoxBinderId(fileBox);
			RetKit retKit = service.unBindBox(ownerId, deviceId, fileBox.getCabinetNum(), fileBox.getNumber());
			renderJson(retKit);
		} else {
			renderJson(RetKit.fail("删除失败"));
		}
	}

	/**
	 * 批量导入
	 */
	public void batAdd() {
		render("batAdd.html");
	}

	/**
	 * 批量导入用户
	 */
	public void batImportTT() {
		final String deviceId = getAttr(FileConstant.deviceId);
		final String[] phoneArray = getParaValues("phone");
		final String[] unitArray = getParaValues("unit");
		final String[] floorArray = getParaValues("floor");
		final String[] roomAddressArray = getParaValues("roomAddress");
		final String[] passwordCardArray = getParaValues("passwordCard");

		boolean flag = Db.tx(new IAtom() {
			boolean save_flag = true;

			@Override
			public boolean run() throws SQLException {
				try {
					if (phoneArray != null) {
						for (int i = 0; i < phoneArray.length; i++) {
							String unit, floor, roomAddress, passwordCard;
							if (unitArray == null) {
								unit = "";
							} else {
								unit = unitArray[i];
							}
							if (floorArray == null) {
								floor = "";
							} else {
								floor = floorArray[i];
							}
							if (roomAddressArray == null) {
								roomAddress = "";
							} else {
								roomAddress = roomAddressArray[i];
							}
							if (passwordCardArray == null) {
								passwordCard = "";
							} else {
								passwordCard = passwordCardArray[i];
							}
							RetKit retKit = FileBoxOwnerService.me.addOrModifyBoxOwner(phoneArray[i], unit, floor,
									roomAddress, deviceId, passwordCard, "", null);
						}
					} else {
						save_flag = false;
					}

				} catch (Exception e) {
					save_flag = false;
					e.printStackTrace();
				}
				return save_flag;
			}
		});
		if (flag) {
			renderJson(RetKit.ok("导入成功"));
		} else {
			if (phoneArray == null) {
				renderJson(RetKit.fail("导入手机号不能为空"));
			} else {
				renderJson(RetKit.fail("导入失败"));
			}
		}
	}

	/**
	 * 选择的excel表信息显示
	 */
	public void batImport() {
		final String deviceId = getAttr(FileConstant.deviceId);
		final List<List<OwnerDeviceTemp>> bindBoxListAll = new ArrayList<List<OwnerDeviceTemp>>();
		final List<OwnerDetail> ownerDetails = new ArrayList<OwnerDetail>();
		boolean flag = Db.tx(new IAtom() {
			boolean save_flag = true;

			@Override
			public boolean run() throws SQLException {
				try {
					UploadFile up = getFile("filePath");
					List<String[]> list = ExcelKit.getExcelData(up.getFile());
					if (list != null && list.size() > 0) { // 将表格标题去掉
						list.remove(0);
					}
					for (String[] strings : list) {
						List<OwnerDeviceTemp> bindBoxList = new ArrayList<OwnerDeviceTemp>();
						OwnerDetail ownerDetail = new OwnerDetail();
						if (strings[0] != null && !"".equals(strings[0])) {
							String phone = strings[0];
							String unit = strings[1];
							String floor = strings[2];
							String roomAddress = strings[3];
							String icCard = strings[4];
							String cabinetNum = strings[5];
							String number = strings[6];
							System.err.println("phone:" + phone + " unit:" + unit + " floor" + floor + " roomAddress:"
									+ roomAddress + " icCard:" + icCard+" cabinetNum:"+cabinetNum+" number:"+number);
							ownerDetail.setPhone(phone);
							ownerDetail.setUnit(unit);
							ownerDetail.setFloor(floor);
							ownerDetail.setRoomAddress(roomAddress);
							ownerDetail.setPasswordCard(icCard);
							ownerDetails.add(ownerDetail);
							OwnerDeviceTemp ownerDeviceTemp = new OwnerDeviceTemp();
							if(!StrKit.isBlank(cabinetNum))
								ownerDeviceTemp.setCabinet_num((new Double(cabinetNum)).intValue());
							if(!StrKit.isBlank(number))
								ownerDeviceTemp.setNumber((new Double(number)).intValue());
							bindBoxList.add(ownerDeviceTemp);
							bindBoxListAll.add(bindBoxList);
							
						}
					}

					if (ownerDetails != null && ownerDetails.size() > 0) {
						for (int i = 0; i < ownerDetails.size(); i++) {
							RetKit retKit = FileBoxOwnerService.me.addOrModifyBoxOwner(ownerDetails.get(i).getPhone(),
									ownerDetails.get(i).getUnit(), ownerDetails.get(i).getFloor(),
									ownerDetails.get(i).getRoomAddress(), deviceId,
									ownerDetails.get(i).getPasswordCard(), "", bindBoxListAll.get(i));
						}
					} else {
						save_flag = false;
					}
				} catch (Exception e) {
					save_flag = false;
					e.printStackTrace();
				}
				return save_flag;
			}
		});
		if (flag) {
			renderJson(RetKit.ok("导入成功"));
		} else {
			renderJson(RetKit.fail("导入失败"));
		}
	}

}
