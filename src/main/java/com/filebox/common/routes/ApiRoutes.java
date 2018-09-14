package com.filebox.common.routes;

import com.filebox.api.protocol.ApiProtocolControl;
import com.filebox.api.admin.ApiAdminController;
import com.filebox.api.common.CommonController;
import com.filebox.api.login.ApiLoginController;
import com.filebox.api.owner.ApiOwnerController;
import com.filebox.api.user.ApiUserController;
import com.filebox.common.inteceptor.ApiAccessTokenInterceptor;
import com.jfinal.config.Routes;

/**
* @Description:TODO(API路由映射)
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月11日
*/
public class ApiRoutes extends Routes {

	@Override
	public void config() {
		addInterceptor(new ApiAccessTokenInterceptor());
		this.add("/api/login", ApiLoginController.class);
		this.add("/api/user", ApiUserController.class);
		this.add("/api/admin", ApiAdminController.class);
		this.add("/api/owner", ApiOwnerController.class);
		this.add("/api/common", CommonController.class);
		this.add("/api/protocol", ApiProtocolControl.class);
	}

}
