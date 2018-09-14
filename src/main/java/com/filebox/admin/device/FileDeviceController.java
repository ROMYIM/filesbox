package com.filebox.admin.device;

import java.text.Normalizer.Form;

import com.filebox.admin.login.LoginService;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileDevice;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

/**
 * @Description:TODO(信报箱首页管理)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月15日
 */
public class FileDeviceController extends Controller {
	public static final FileDeviceService service = FileDeviceService.me;

	/*public void index() {
		String sessionId = getCookie(LoginService.sessionIdName);
		Ret ret = service.list(sessionId);
		setAttr("list", ret.get("list"));
		// 声明首页，便于在模板中去掉菜单栏
		setAttr("index", true);
		render("index.html");
	}*/
	
	public void index() {
		String sessionId = getCookie(LoginService.sessionIdName);
		int ps = getParaToInt("pageSize",10);
		int pn = getParaToInt("pageNum",1);
		String deviceName = getPara("deviceName");
		String operatorName = getPara("operatorName");
		if (StrKit.notBlank(deviceName) && deviceName.equals("null")) {
			deviceName =null;
		}
		if (StrKit.notBlank(operatorName) && operatorName.equals("null")) {
			operatorName = null;
		}
		JMap para = JMap.create().set("deviceName", deviceName).set("operatorName",operatorName);
		keepPara();
		Ret ret = service.listByPage(sessionId, ps, pn, para);
		setAttr("page", ret.get("page"));
		render("index.html");
	}

	// 缓存快递柜ID，提供给二级菜单调用
	public void setFileDeviceId() {
		String deviceId = getPara();
		if (deviceId == null) {
			redirect("/device");
			return;
		}
		String sessionId = getCookie(LoginService.sessionIdName);
		service.setFiledeviceId(sessionId, deviceId);
		redirect("/filebox");
	}
	
	/**
	 * 设置运营商
	 */
	@Before(FileDeviceValidator.class)
	public void deviceSetting(){
		String deviceId = getPara();
		setAttr("device", service.getDevice(deviceId));
		render("form.html");
	}
	
	/**
	 * 更改运营商
	 */
	@Before(FileDeviceValidator.class)
	public void updateDevice(){
		RetKit retKit = service.updateDevice(getModel(FileDevice.class, "device"));
		renderJson(retKit);
	}
	
	
	/**
	 * 删除设备
	 */
	@Before(FileDeviceValidator.class)
	public void deleteDevice(){
		String deviceId =getPara();
		renderJson(service.deleteDevice(deviceId));
	}

}
