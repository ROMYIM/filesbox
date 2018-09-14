package com.filebox.admin.account;

import com.filebox.admin.login.LoginService;
import com.filebox.common.inteceptor.AdminLoginInteceptor;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;

/**
 * @Description:TODO(管理员管理)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月20日
 */
public class AccountController extends Controller {
	static final AccountService service = AccountService.me;

//	public void index() {
//		RetKit ret = service.list();
//		setAttr("list", ret.get("list"));
//		render("index.html");
//	}
	public void index() {
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		String name = getPara("name");
		String phone = getPara("phone");
		if (StrKit.notBlank(name) && name.equals("null")) {
			name =null;
		}
		if (StrKit.notBlank(phone) && phone.equals("null")) {
			phone = null;
		}
		JMap para = JMap.create().set("name", name).set("phone", phone);
		keepPara();
		RetKit ret = service.listByPage(ps, pn, para);
		setAttr("page", ret.get("page"));
		render("index.html");
	}

	/**
	 * 禁用
	 */
	public void disable() {
		RetKit retKit = service.disable(getParaToInt("id"));
		renderJson(retKit);
	}

	/**
	 * 启用
	 */
	public void undisable() {
		RetKit retKit = service.undisable(getParaToInt("id"));
		renderJson(retKit);
	}

	/**
	 * 添加
	 */
	public void add(){
		render("account_form.html");
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		RetKit retKit = service.delete(getParaToInt("id"));
		renderJson(retKit);
	}

	/**
	 * 编辑-查看
	 */
	public void edit() {
		setAttr("account", service.getAccount(getPara()));
		render("account_form.html");
	}

	/**
	 * 查找箱子
	 */
	public void searchCablinets() {
		String searchStr = getPara("searchStr");
		RetKit retKit = service.searchCablinets(searchStr);
		renderJson(retKit);
	}
	
	/**
	 *新增管理员和绑定的箱子 
	 */
	@Before(AccountValidator.class)
	public void save(){
		RetKit retKit = service.save(getModel(Account.class), getPara("devices"));
		renderJson(retKit);
	}
	
	/**
	 * 修改管理员和绑定的箱子
	 */
	@Before(AccountValidator.class)
	public void update(){
		RetKit retKit = service.update(getModel(Account.class), getPara("devices"));
		renderJson(retKit);
	}

	/**
	 * 发送验证码
	 */
	@Clear
	@Before(AdminLoginInteceptor.class)
	public void sendVerifyCode() {
		Account loginAccount = getAttr(LoginService.loginAccountCacheName);
		String sessionId = getCookie(LoginService.sessionIdName);
		RetKit retKit = service.sendVerifyCode(loginAccount, sessionId);
		renderJson(retKit.success() ? retKit.ok("发送成功") : retKit);
	}
}
