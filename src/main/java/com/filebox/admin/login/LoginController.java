package com.filebox.admin.login;

import com.filebox.common.kit.RetKit;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.weixin.sdk.kit.IpKit;

import sun.print.resources.serviceui;

/**
 * @Description:TODO(后台登录Controller)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月11日
 */
@Clear
public class LoginController extends Controller {

	public final static LoginService service = LoginService.me;

	public void index() {
		render("login.html");
	}

	/**
	 * 登录
	 */

	@Before(LoginValidator.class)
	public void doLogin() {
		boolean keepLogin = getParaToBoolean("keepLogin", false);
		String loginIp = IpKit.getRealIp(getRequest());
		RetKit retKit = service.login(getPara("phone"), getPara("password"), keepLogin, loginIp);
		if (retKit.success()) {
			String sesssionId = retKit.getStr(LoginService.sessionIdName);
			int maxAgeInSeconds = retKit.getAs("maxAgeInSeconds");
			setCookie(LoginService.sessionIdName, sesssionId, maxAgeInSeconds, true);
			setAttr(LoginService.loginAccountCacheName, retKit.get(LoginService.loginAccountCacheName));
			retKit.set("returnUrl", getPara("returnUrl", "device")); // 如果returnUrl存在则跳过去，否则跳去首页
		}
		renderJson(retKit);
	}

	/**
	 * 退出登录
	 */
	public void logout() {
		service.logout(getCookie(LoginService.sessionIdName));
		removeCookie(LoginService.sessionIdName);
		redirect("/");
	}
}
