package com.filebox.admin.filebox.sepcialuser;

import java.util.List;

import com.filebox.admin.filebox.owner.AddOrModifyBoxOwnerValidator;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.inteceptor.DeviceInterceptor;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.SpecialUser;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

/**
* @Description:TODO(特殊用户管理)
* @author 作者 : jinghui.su
* @date 创建时间：2017年7月4日
*/
@Before(DeviceInterceptor.class)
public class SpecialUserController extends Controller {
	public static final SpecialUserService service = SpecialUserService.me;
	
	public void index(){
		String deviceId = getAttr(FileConstant.deviceId);
		setAttr("list", service.specialUserList(deviceId));
		List<SpecialUser> kist = service.noTerminalSpecialUserList(deviceId);
		setAttr("noTerminalList", service.noTerminalSpecialUserList(deviceId));
		render("index.html");
	}
	
	/**
	 * 新增或修改特殊用户
	 */
	@Before(AddOrModifyBoxOwnerValidator.class)
	public void addOrModifySpecialUser(){
		String deviceId = getAttr(FileConstant.deviceId);
		String phone = getPara("phone");
		String icCard = getPara("icCard");
		int status = getParaToInt("status", SpecialUser.STATUS_OK);
		RetKit retKit = service.addOrModifySpecialUser(phone, deviceId,icCard, status);
		renderJson(retKit);
	}
	
	
	
	/**
	 * 禁用特殊用户
	 */
	public void lockSpecialUser(){
		int specialUserId = getParaToInt();
		RetKit retKit  = service.updateSpecialUserStatus(specialUserId, SpecialUser.STATUS_LOCK);
		renderJson(retKit);
	}
	
	/**
	 * 启用特殊用户
	 */
	public void unlockSpecialUser(){
		int specialUserId = getParaToInt();
		RetKit retKit  = service.updateSpecialUserStatus(specialUserId, SpecialUser.STATUS_OK);
		renderJson(retKit);
	}
	
	/**
	 * 删除特殊用户
	 */
	public void deleteSpecialUser(){
		String deviceId = getAttr(FileConstant.deviceId);
		int specialUserId = getParaToInt();
		RetKit retKit = service.deleteSpecialUser(specialUserId,deviceId);
		renderJson(retKit);
	}
	
	/**
	 * 添加特殊用户
	 */
	public void add(){
		render("form.html");
	}
	
	/**
	 * 更新特殊用户
	 */
	public void update(){
		int specialUserId = getParaToInt();
		setAttr("specialUser", service.SpecialUserInfo(specialUserId));
		render("form.html");
	}
	
	/**
	 * 更新终端特殊用户到终端
	 */
	public void sendSpecialToTerminal(){
		String deviceId = getAttr(FileConstant.deviceId);
		RetKit retKit = service.sendSpecialToTerminal(deviceId);
		renderJson(retKit);
	}
}
