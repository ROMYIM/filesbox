package com.filebox.api.phoneadv;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import java.util.List;

import com.filebox.common.inteceptor.ApiUserAccessTokenIntercepter;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.AdvertisingPhone;

public class ApiPhoneAdvController extends Controller {
	
	public static final ApiPhoneAdvService service = new ApiPhoneAdvService();
	
	@Clear
	@Before(ApiUserAccessTokenIntercepter.class)
	public void AdvPhoneList(){
		List<AdvertisingPhone> list;
		list = service.getList();
		renderJson(RetKit.ok("data", list));
	}

	/**
	 * 手机开锁广告
	 */
	public void OpenPhoneAdv(){
		AdvertisingPhone padvertising=service.getOpenList();
		renderJson(RetKit.ok("data", padvertising));
	}
}
