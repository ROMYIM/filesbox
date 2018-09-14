package com.filebox.admin.account;

import com.filebox.admin.login.LoginService;
import com.filebox.common.inteceptor.AdminLoginInteceptor;
import com.filebox.common.model.Account;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

/**
* @Description:TODO()
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月19日
*/
public class MyController extends Controller {
	static final AccountService service = AccountService.me;
	
	//重置密码
	@Clear
	@Before(AdminLoginInteceptor.class)
	public void reset(){
		Account loginAccount = getAttr(LoginService.loginAccountCacheName);
		setAttr("loginAccount", loginAccount);
		render("update_password.html");
	}
	
	//重置密码
	@Clear
	@Before(value={AdminLoginInteceptor.class,UpdateValidator.class})
	public void updatePassword(){
		Account account = getModel(Account.class);
		Ret ret = service.updatePassword(account,getPara("verifyCode"));
		renderJson(ret);
	}
}
