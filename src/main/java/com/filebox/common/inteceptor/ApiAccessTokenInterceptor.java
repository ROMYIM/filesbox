package com.filebox.common.inteceptor;

import org.apache.log4j.Logger;

import com.filebox.api.login.ApiLoginService;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.constant.SysConstant;
import com.filebox.common.kit.RetKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

/**
 * @Description:TODO(API拦截器)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月11日
 */
public class ApiAccessTokenInterceptor implements Interceptor {

	private static final Logger log = Logger.getLogger(ApiAccessTokenInterceptor.class);

	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		String accessToken = controller.getHeader(FileConstant.accessToken);
		String deviceId = controller.getHeader(FileConstant.deviceId);
		controller.setAttr(FileConstant.deviceId, deviceId);

		RetKit retKit = new RetKit();
		if (accessToken == null) {
			retKit.setFail(SysConstant.CODE_502, SysConstant.CODE_502_MSG);
			controller.renderJson(retKit);
			return;
		}

		Record loginAccount = ApiLoginService.me.getLoginAccount(accessToken);
		if (loginAccount == null) {
			retKit.setFail(SysConstant.CODE_503, SysConstant.CODE_503_MSG);
		}
		controller.setAttr(FileConstant.loginAccount, loginAccount);
		try {
			inv.invoke();
		} catch (Exception e) {
			doLog(inv, e);
			controller.renderJson(RetKit.fail(SysConstant.CODE_500, SysConstant.CODE_500_MSG));
		}

	}

	private void doLog(Invocation inv, Exception e) {
		StringBuilder sb = new StringBuilder("\n---Exception Log Begin---\n");
		sb.append("Controller:").append(inv.getController().getClass().getName()).append("\n");
		sb.append("Method:").append(inv.getMethodName()).append("\n");
		sb.append("Exception Type:").append(e.getClass().getName()).append("\n");
		sb.append("Exception Details:");
		log.error(sb.toString(), e);
	}

}
