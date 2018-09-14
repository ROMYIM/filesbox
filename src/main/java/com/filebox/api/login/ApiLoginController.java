package com.filebox.api.login;

import com.filebox.common.constant.FileConstant;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;

/**
 * @Description:TODO(登录控制)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月24日
 */
public class ApiLoginController extends Controller {
	public final static ApiLoginService srv = ApiLoginService.me;

	@Before(ApiLoginValidator.class)
	@Clear
	public void doLogin() {
		JMap map = new JMap();
		map.set("phone", getPara("phone"));
		map.set("password", getPara("password"));
		map.set("type", getParaToInt("type", 1));
		map.set(FileConstant.deviceId, getHeader(FileConstant.deviceId));
		renderJson(srv.doLogin(map));
	}

	public void logout() {
		String accessToken = getAttr(FileConstant.accessToken);
		renderJson(srv.logout(accessToken));
	}
	
}
