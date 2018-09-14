package com.filebox.common.inteceptor;

import com.filebox.api.login.ApiLoginService;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.constant.SysConstant;
import com.filebox.common.kit.RetKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * @Description:TODO(移动端api请求拦截器)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年6月29日
 */
public class ApiUserAccessTokenIntercepter implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller con = inv.getController();
		String accessToken = con.getHeader(FileConstant.accessToken);
		RetKit ret = new RetKit();
		if (accessToken == null) {
			ret.setFail(SysConstant.CODE_502, SysConstant.CODE_502_MSG);
			con.renderJson(ret);
			return;
		}
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken); // 首先在缓存里面查看移动端用户是否登录失效，如果失效再看看数据库里面的token是否失效
		if (loginUser == null) {
			Record ownerToken = Db.findFirst("select * from owner_token where token = ?", accessToken); // 缓存失效
																									// 尝试看看数据库里面的token是否存在
			if (ownerToken == null) {
				ret.setFail(SysConstant.CODE_503, SysConstant.CODE_503_MSG);
				con.renderJson(ret);
				return;
			} else {
				String ownerId = ownerToken.get("owner_id")+"";
				if (StrKit.notBlank(ownerId)) {
					loginUser = Db.findFirst("select * from file_box_owner where id = ?",ownerId);
					if(loginUser != null){
						loginUser.set(FileConstant.accessToken, accessToken);
						CacheKit.put(FileConstant.apiAccountCache, accessToken, loginUser); 
					}
				}
			}

		}
		con.setAttr(FileConstant.loginUser, loginUser);
		try {
			inv.invoke();
		} catch (Exception e) {
			e.printStackTrace();
			con.renderJson(RetKit.fail(SysConstant.CODE_500, SysConstant.CODE_500_MSG));
		}
	}

}
