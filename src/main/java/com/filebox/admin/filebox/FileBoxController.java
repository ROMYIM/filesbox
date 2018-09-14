package com.filebox.admin.filebox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.filebox.admin.device.FileDeviceService;
import com.filebox.admin.device.OwnerDeviceService;
import com.filebox.admin.filebox.owner.AddOrModifyBoxOwnerValidator;
import com.filebox.admin.filebox.owner.FileBoxOwnerService;
import com.filebox.api.owner.OwnerDeviceTemp;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.inteceptor.DeviceInterceptor;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileBoxOwner;
import com.filebox.common.model.OwnerDevice;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @Description:TODO()
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月16日
 */
@Before(DeviceInterceptor.class)
public class FileBoxController extends Controller {
	public static final FileBoxService service = FileBoxService.me;

	public void index() {
		String deviceId = getAttrForStr(FileConstant.deviceId);
		setAttr("device", FileDeviceService.me.getDevice(deviceId));
		setAttr("list", service.boxListWithOwnerInfo(deviceId));
		render("index.html");
	}

	/**
	 * 报修
	 */
	public void repair() {
		FileBox model = service.getFileBox(getParaToInt());
		setAttr("filebox", model);
		render("repair_form.html");
	}

	/**
	 * 报修界面执行更新操作
	 */
	public void repairUpdate() {
		String deviceId = getAttrForStr(FileConstant.deviceId);
		RetKit retKit = service.repairUpdate(deviceId, getModel(FileBox.class, ""));
		renderJson(retKit);
	}

	/**
	 * 开门
	 */
	public void openFileBox() {
		int boxId = getParaToInt("id", 0);
		RetKit retKit = service.openFileBox(boxId);
		renderJson(retKit);
	}
	
	/**
	 * 一个箱子绑定用户
	 */
	public void addOrUpdate(){
		String deviceId = getAttr(FileConstant.deviceId);
		int boxId = getParaToInt();
		FileBox fileBox = service.getFileBox(boxId);
		if(fileBox != null){
			int ownerId = OwnerDeviceService.me.getBoxBinderId(fileBox);
			Record test = FileBoxOwnerService.me.fileBoxOwnerInfo(ownerId,deviceId);
			setAttr("owner",FileBoxOwnerService.me.fileBoxOwnerInfo(ownerId,deviceId));
			setAttr("fileBoxInfo", fileBox);
		}
		
		render("bind_info.html");
	}
	
	/**
	 *  增加和修改箱子绑定人(一个箱子被一个用户绑定)
	 */
	@Before(AddOrModifyBoxOwnerValidator.class)
	public void addOrUpdateBoxOwner(){
		String deviceId = getAttr(FileConstant.deviceId);
		String phone = getPara("phone");
		String unit = getPara("unit");
		String floor = getPara("floor");
		String roomAddress = getPara("roomAddress");
		String passwordCard = getPara("passwordCard");
		String passwordFingerPrint = getPara("passwordFingerPrint");
		int cabinetNum = getParaToInt("cabinetNum",0);
		int number = getParaToInt("number",0);
		
		List<OwnerDeviceTemp> bindBoxList = new ArrayList<>();
		FileBoxOwner fileBoxOwner = FileBoxOwnerService.me.fileBoxOwnerInfo(phone);  //查找归属人是否存在
		if(fileBoxOwner != null){
			List<OwnerDevice> ownerDevices = FileBoxOwnerService.me.getOwnerBindBoxsList(fileBoxOwner.getId(), deviceId);
			if(ownerDevices != null){
				Iterator iterator = ownerDevices.iterator();
				while (iterator.hasNext()) {
					OwnerDevice ownerDevice = (OwnerDevice) iterator.next();
					if(ownerDevice.getCabinetNum().equals(cabinetNum) && ownerDevice.getNumber().equals(number)){
						iterator.remove(); //移除编辑的箱子
					}else{
						OwnerDeviceTemp temp = new OwnerDeviceTemp();
						temp.setCabinet_num(ownerDevice.getCabinetNum());
						temp.setNumber(ownerDevice.getNumber());
						bindBoxList.add(temp);
					}
				}
			}
		}
		
		OwnerDeviceTemp deviceTemp = new OwnerDeviceTemp();  //加上编辑的箱子
		deviceTemp.setCabinet_num(cabinetNum);
		deviceTemp.setNumber(number);
		bindBoxList.add(deviceTemp);
		
		
		RetKit retKit = FileBoxOwnerService.me.addOrModifyBoxOwner(phone, unit, floor, roomAddress, deviceId,
				passwordCard,passwordFingerPrint,bindBoxList);
		renderJson(retKit);
	}
}
