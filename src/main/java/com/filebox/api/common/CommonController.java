package com.filebox.api.common;

import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @Description:TODO(公共层控制)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月9日
 */
public class CommonController extends Controller {
	public final static CommonService srv = CommonService.me;

	/**
	 * 忘记密码发送验证码
	 */
	@Clear
	public void sendForgetPwdCode() {
		RetKit retKit = srv.sendForgetPwdCode(getPara("phone"));
		renderJson(retKit);
	}

	/**
	 * 忘记密码校验密码
	 */
	@Clear
	public void updatePassword() {
		String role = getPara("role");
		String phone = getPara("phone");
		String password = getPara("password");
		String vertifyCode = getPara("code");
		RetKit retKit = srv.updatePassword(role, phone, password, vertifyCode);
		renderJson(retKit);
	}

	/**
	 * 修改密码
	 */
	@Clear
	public void changePassword() {
		String role = getPara("role");
		String phone = getPara("phone");
		String oldPassword = getPara("oldPassword");
		String newPassword = getPara("newPassword");
		RetKit retKit = srv.changePassword(role, phone, oldPassword, newPassword);
		renderJson(retKit);
	}
	
	/**
	 * 获取Banner
	 */
	@Before(BannerValidator.class)
	@Clear
	public void getBanner(){
		String deviceId =getHeader(FileConstant.deviceId);
		renderJson(srv.getBanner(deviceId));
	}
}
