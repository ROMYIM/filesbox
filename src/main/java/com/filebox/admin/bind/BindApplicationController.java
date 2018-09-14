package com.filebox.admin.bind;

import java.util.Date;
import java.util.List;

import com.filebox.admin.device.OwnerDeviceService;
import com.filebox.admin.filebox.FileBoxService;
import com.filebox.admin.filebox.owner.FileBoxOwnerService;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.inteceptor.DeviceInterceptor;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.BindApplication;
import com.filebox.common.model.FileBox;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.Record;

@Before(DeviceInterceptor.class)
public class BindApplicationController extends Controller {

	static final BindApplicationService srv = BindApplicationService.me;
	static final FileBoxService service =FileBoxService.me;
	
	public BindApplicationController() {
		// TODO Auto-generated constructor stub
	}


	public void index() {
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		String deviceId = getAttrForStr(FileConstant.deviceId);
		setAttr("page", srv.listByPage(pn, ps, deviceId));
		render("index.html");
	}
	
	/**
	 * 由管理员核对信息再提交。
	 */
	public void checkInfo(){
		int id = getParaToInt();
		BindApplication info = srv.getAppicationInfo(id);
		if (info.getCode() == BindApplication.BIND_CODE) {
			Integer boxId = info.getBoxId();
			FileBox fileBox = service.getFileBox(boxId);
			Integer ownerId = info.getOwnerId();
			setAttr("owner", srv.applicationDetail(ownerId, boxId));
			setAttr("fileBoxInfo", fileBox);
			render("bind_info.html");
		} else {
			redirect("/bind");
		}
	}
	
	/**
	 * 绑定操作
	 * 成功后，在owner_device添加信息
	 * 并删除bind_application中的对应的申请信息
	 */
	public void bindBox() {
		JMap map = new JMap();
		map.set("ownerId", getParaToInt("ownerId"));
		map.set("boxId", getParaToInt("boxId"));
		map.set("deviceId", getPara("deviceId"));
		map.set("unit", getPara("unit"));
		map.set("floor", getPara("floor"));
		map.set("roomAddress", getPara("roomAddress"));
		map.set("passwordCard", getPara("passwordCard"));
		if (srv.bindBox(map).success()) {
			Date timeStamp = BindApplication.getCurrentTimeStamp();
			srv.updateApplication(getParaToInt("ownerId"), getParaToInt("boxId"), timeStamp);
			renderJson(RetKit.ok());
		} else {
			renderJson(RetKit.fail());
		}
	}
	
	/**
	 * 解绑操作
	 * 成功后删除owner_device和bind_application对应的信息
	 */
	public void unbindBox() {
		int id = getParaToInt();
		BindApplication info = srv.getAppicationInfo(id);
		if (info.getCode() == BindApplication.UNBIND_CODE) {
			Integer boxId = info.getBoxId();
			Integer ownerId = info.getOwnerId();
			if (srv.unbindBox(ownerId, boxId).success()) {
				Date timeStamp = BindApplication.getCurrentTimeStamp();
				srv.updateApplication(id, timeStamp);
				renderJson(RetKit.ok());
			} else {
				renderJson(RetKit.fail());
			}
		} else {
			renderJson(RetKit.fail("操作错误"));
		}
	}
}
