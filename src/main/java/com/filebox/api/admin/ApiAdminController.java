package com.filebox.api.admin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.filebox.admin.device.FileDeviceService;
import com.filebox.admin.device.OwnerDeviceService;
import com.filebox.admin.filebox.FileBoxService;
import com.filebox.admin.filebox.owner.AddOrModifyBoxOwnerValidator;
import com.filebox.admin.filebox.owner.BoxOwnerAddOrModifyValidator;
import com.filebox.admin.filebox.owner.FileBoxOwnerService;
import com.filebox.admin.filebox.sepcialuser.SpecialUserService;
import com.filebox.api.login.ApiLoginValidator;
import com.filebox.api.owner.OwnerDeviceTemp;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.OwnerDevice;
import com.filebox.common.model.SpecialUser;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.json.Jackson;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @Description:TODO(管理员控制器)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月24日
 */
public class ApiAdminController extends Controller {
	public final static ApiAdminService srv = ApiAdminService.me;

	/**
	 * 获取文件箱归属人员列表
	 */
	public void getFileBoxOwnerList() {
		String deviceID = getAttr(FileConstant.deviceId);
		RetKit retKit = srv.getFileBoxOwnerList(deviceID, getParaToInt("pageSize", 10), getParaToInt("pageNum", 1));
		renderJson(retKit);
	}

	/**************************** 硬件模块 ********************************************/
	/**
	 * 硬件模块 - 更新设备
	 */
	@Clear
	public void addNewDevice() {
		String deviceId = getHeader(FileConstant.deviceId);
		checkDeviceIsExit(deviceId);
		String data = getPara("data");
		List<Device> list = JSON.parseArray(data, Device.class);
		renderJson(srv.addNewDevice(list, deviceId));
	}

	/**
	 * 检测设备是否存在，不存在就新增
	 */
	private void checkDeviceIsExit(String deviceId) {
		FileDevice device = FileDeviceService.me.getDevice(deviceId);
		if (device == null) {
			FileDevice dev = new FileDevice();
			dev.setId(deviceId);
			dev.save();
			// 初始化设备标准
			ApiAdminService.me.createPostAge(deviceId);
		}
	}

	/**
	 * 硬件模块 - 更改信报箱箱子状态
	 */
	@Clear
	public void updateBoxStatus() {
		String deviceId = getHeader(FileConstant.deviceId);
		String data = getPara("data");
		Device cabinet = JsonKit.parse(data, Device.class);
		RetKit retKit = FileBoxService.me.updateBoxsStatus(deviceId, cabinet);
		renderJson(retKit);
	}

	/**
	 * 更改信报箱-使用状态
	 */
	@Clear
	public void updateDeviceStatus() {
		String deviceId = getHeader(FileConstant.deviceId);
		int status = getParaToInt("status");
		RetKit retKit = FileBoxService.me.updateDeviceStatus(deviceId, status);
		renderJson(retKit);
	}

	/**
	 * 信报箱列表
	 */
	@Clear
	public void boxList() {
		String deviceId = getHeader(FileConstant.deviceId);
		List<FileBox> list = FileBoxService.me.boxList(deviceId);
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 信报箱列表(包含绑定人信息)
	 */
	@Clear
	public void boxListWithOwnerInfo() {
		String deviceId = getHeader(FileConstant.deviceId);
		List<Record> list = FileBoxService.me.boxListWithOwnerInfo(deviceId);
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 箱子列表（未使用和指定owner_id的）
	 */
	@Clear
	public void boxListNotUseInfoOrIncludeSepcialOwner() {
		String deviceId = getHeader(FileConstant.deviceId);
		int ownerId = getParaToInt("owner_id", 0);
		List<Record> list = FileBoxService.me.boxListNotUseInfoOrIncludeSepcialOwner(deviceId, ownerId);
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 信报箱归属人列表
	 */
	@Clear
	public void boxOwnerList() {
		String deviceId = getHeader(FileConstant.deviceId);
		List<Record> list = FileBoxOwnerService.me.boxOwnerList(deviceId);
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 信报箱归属人列表（一个用户只显示一条）
	 */
	@Clear
	public void boxOwnerListByGroup() {
		String deviceId = getHeader(FileConstant.deviceId);
		List<Record> list = FileBoxOwnerService.me.boxOwnerListByGroup(deviceId);
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 信报箱归属人所含设备列表
	 */
	@Clear
	public void ownerDeviceList() {
		String deviceId = getHeader(FileConstant.deviceId);
		String ownerId = getPara("owner_id");
		List<Record> list = FileBoxOwnerService.me.ownerDeviceList(ownerId, deviceId);
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 增加和修改箱子绑定人(一个箱子被一个人绑定)
	 */
	@Before(BoxOwnerAddOrModifyValidator.class)
	@Clear
	public void boxOwnerAddOrModity() {
		String deviceId = getHeader(FileConstant.deviceId);
		String phone = getPara("phone");
		String unit = getPara("unit");
		String floor = getPara("floor");
		String roomAddress = getPara("roomAddress");
		String passwordCard = getPara("passwordCard");
		String passwordFingerPrint = getPara("passwordFingerPrint");
		int cabinet_num = getParaToInt("cabinet_num");
		int number = getParaToInt("number");
		RetKit retKit = FileBoxOwnerService.me.addBoxOwner(phone, unit, floor, roomAddress, deviceId,passwordCard,passwordFingerPrint, cabinet_num,
				number);
		renderJson(retKit);
	}

	/**
	 * 增加和修改箱子绑定人(一个人绑定多个箱子)
	 */
	@Before(AddOrModifyBoxOwnerValidator.class)
	@Clear
	public void addOrModifyBoxOwner() {
		String deviceId = getHeader(FileConstant.deviceId);
		String phone = getPara("phone");
		String unit = getPara("unit");
		String floor = getPara("floor");
		String roomAddress = getPara("roomAddress");
		String passwordCard = getPara("passwordCard");
		String passwordFingerPrint = getPara("passwordFingerPrint");
		String bindList = getPara("bindBoxList");
		List<OwnerDeviceTemp> bindBoxList = JSON.parseArray(bindList, OwnerDeviceTemp.class);
		RetKit retKit = FileBoxOwnerService.me.addOrModifyBoxOwner(phone, unit, floor, roomAddress, deviceId,
				passwordCard,passwordFingerPrint, bindBoxList);
		renderJson(retKit);
	}

	/**
	 * 获取设备信息
	 */
	@Clear
	public void getDeviceInfo() {
		String deviceId = getHeader(FileConstant.deviceId);
		renderJson(RetKit.ok("data", srv.getDeviceInfo(deviceId)));
	}

	/**
	 * 修改
	 */
	@Clear
	public void repair() {
		String deviceId = getHeader(FileConstant.deviceId);
		FileBox box = getModel(FileBox.class, "");
		box.setDeviceId(deviceId);
		RetKit retKit = FileBoxService.me.repair(box);
		renderJson(retKit);
	}

	/**
	 * 删除归属人指定设备
	 */
	@Clear
	public void deleteOwnerDevice() {
		String deviceId = getHeader(FileConstant.deviceId);
		int ownerId = getParaToInt("owner_id");
		RetKit retKit = OwnerDeviceService.me.deleteOwnerDevice(ownerId, deviceId);
		renderJson(retKit);
	}
	
	/**
	 * 远程开锁（临时开放给手机端）
	 */
	@Clear
	public void openFileBox(){
		String deviceId = getHeader(FileConstant.deviceId);
		int cabinetNum = getParaToInt("cabinetNum", 0);
		int number = getParaToInt("number",0);
	
		RetKit ret = FileBoxService.me.openFileBox(deviceId, cabinetNum, number);
		
		renderJson(ret);
	}
	/**
	 * 给QT登录
	 */
	@Clear
	public void qtLogin(){
		String phone =getPara("phone", "");
		String password = getPara("password", "");
		RetKit retKit;
		if(StrKit.notBlank(phone) && phone.equals("15521811993") && StrKit.notBlank(password) && password.equals("123456")){
			retKit = RetKit.ok("data", "0e1751d4df646298");
		}else{
			retKit = RetKit.fail("登录失败");
		}
		renderJson(retKit);
	}
	
	/**
	 * 给QT新增卡用户
	 */
	@Clear
	public void qtAddOrModifySpecialUser(){
		String deviceId =getPara("deviceId", "");
		String icCard = getPara("icCard","");
		String phone = getPara("userPhone","");
		RetKit retKit;
		if(StrKit.isBlank(deviceId)){
			retKit = RetKit.fail("deviceId不能为空");
		}else if(StrKit.isBlank(icCard)){
			retKit = RetKit.fail("icCard不能为空");
		}else if(StrKit.isBlank(phone)){
			retKit = RetKit.fail("手机号不能为空");
		}else{
			int status =  SpecialUser.STATUS_OK;
			retKit = SpecialUserService.me.addOrModifySpecialUser(phone, deviceId,icCard, status);
		}
		renderJson(retKit);
	}

}
