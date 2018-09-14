package com.filebox.common.inteceptor;


import com.filebox.admin.login.LoginService;
import com.filebox.common.model.Account;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.IpKit;

/**
* @Description:TODO()
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月11日
*  * 从 cookie 中获取 sessionId，如果获取到则根据该值使用 LoginService
 * 得到登录的 Account 对象 ---> loginAccount，供后续的流程使用
 * 
 * 注意：将此拦截器设置为后台全局拦截器，所有 后台 action 都需要
*/
public class AdminLoginInteceptor implements Interceptor {
	
	public static final String adminAuhCache = "adminAuh";

	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		Account loginAccount = null;
		String sessionId = controller.getCookie(LoginService.sessionIdName);
		boolean isLogin =false;
		if(sessionId != null){
			loginAccount = LoginService.me.getLoginAccountWithSessionId(sessionId);
			if(loginAccount == null){
				String loginIp = IpKit.getRealIp(controller.getRequest());
				loginAccount =  LoginService.me.loginWithSessionId(sessionId, loginIp);
			}
			if(loginAccount != null){
				//用户登录账号
				isLogin = true;
				controller.setAttr(LoginService.loginAccountCacheName, loginAccount);
				/**
				 * 输出菜单到前台页面
				 * 暂时这样写。还没想到有更好的办法。
				 */
			}else{
				controller.removeCookie(LoginService.sessionIdName);   
			}
		}
		if(isLogin){
			inv.invoke();
		}else{
			String queryString = inv.getController().getRequest().getQueryString();
			if(StrKit.isBlank(queryString)){
				controller.redirect("/login?return=" + inv.getActionKey());
			}else{
				controller.redirect("/login?returnUrl=" + inv.getActionKey() + "?" + queryString);
			}
		}
	}

}
