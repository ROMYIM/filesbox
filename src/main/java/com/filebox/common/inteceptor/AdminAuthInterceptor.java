package com.filebox.common.inteceptor;

import java.util.Date;
import java.util.List;

import com.alibaba.druid.support.logging.Log;
import com.filebox.admin.login.LoginService;
import com.filebox.common.model.Account;
import com.filebox.common.model.AdminLog;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.kit.IpKit;

/**
 * @Description:TODO(后台权限管理拦截器)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月11日
 */
public class AdminAuthInterceptor implements Interceptor {

	public static final String adminAuhCache = "adminAuh";

	/**
	 * 校验这个登录人的身份是否有权限访问这个方法
	 */
	private Boolean validate(Account loginAccount, String sessionId, String actionKey) {
		List<Record> list = Db.findByCache(adminAuhCache, sessionId,
				"select b.* from sys_role_menu a left join sys_menu b on a.menu_id = b.id where role_id = ?",
				loginAccount.getRole());
		for (Record record : list) {
			String url = record.getStr("url");
			if (actionKey.contains(url)) {
				return true;
			}
		}
		return false;
	}

	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		Account loginAccount = inv.getController().getAttr(LoginService.loginAccountCacheName);
		String sessionId = controller.getCookie(LoginService.sessionIdName);
		String actionKey = inv.getActionKey();
		if (validate(loginAccount, sessionId, actionKey)) {
			inv.invoke();
			saveAdminActionLog(inv.getController());
		} else {
			inv.getController().renderError(404);
		}
	}

	/**
	 * 报错日志
	 */
	private void saveAdminActionLog(Controller con) {
		AdminLog model = new AdminLog();
		model.setIp(IpKit.getRealIp(con.getRequest()));
		model.setAction(con.getRequest().getRequestURI());
		model.setUsername(con.getCookie(LoginService.loginPhone));
		model.setSysupdate(new Date());
		model.save();

	}
}
