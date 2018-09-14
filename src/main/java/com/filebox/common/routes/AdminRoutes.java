package com.filebox.common.routes;

import com.filebox.admin.protocol.ProtocolController;
import com.filebox.admin.repair.RepairApplicationController;
import com.filebox.admin.account.AccountController;
import com.filebox.admin.account.MyController;
import com.filebox.admin.adv.AdvertisingController;
import com.filebox.admin.bind.BindApplicationController;
import com.filebox.admin.bonuspoints.BonusPointsController;
import com.filebox.admin.device.FileDeviceController;
import com.filebox.admin.filebox.FileBoxController;
import com.filebox.admin.filebox.owner.FileBoxOwnerController;
import com.filebox.admin.filebox.sepcialuser.SpecialUserController;
import com.filebox.admin.information.InformationController;
import com.filebox.admin.login.LoginController;
import com.filebox.admin.model.ModelController;
import com.filebox.admin.openphoneadv.OpenPhoneAdvController;
import com.filebox.admin.operator.OperatorController;
import com.filebox.admin.phoneadv.PhoneavertisingController;
import com.filebox.common.inteceptor.AdminAuthInterceptor;
import com.filebox.common.inteceptor.AdminLoginInteceptor;
import com.jfinal.config.Routes;

/**
 * @Description:TODO(后台路由映射)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月11日
 */
public class AdminRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/_view");

		addInterceptor(new AdminLoginInteceptor());
		addInterceptor(new AdminAuthInterceptor());
		this.add("/bind", BindApplicationController.class);
		this.add("/phoneadv", PhoneavertisingController.class);
		this.add("/login", LoginController.class);// 登陆
		this.add("/", LoginController.class, "login");// 登陆
		this.add("/device", FileDeviceController.class);
		this.add("/filebox", FileBoxController.class);
		this.add("/owner", FileBoxOwnerController.class);
		this.add("/my",MyController.class,"account");
		this.add("/account",AccountController.class);
		this.add("/operator",OperatorController.class);
		this.add("/model",ModelController.class);
		this.add("/adv",AdvertisingController.class);
		this.add("/specialUser",SpecialUserController.class);
		this.add("/information", InformationController.class);
		this.add("/protocol",ProtocolController.class);
		this.add("/repair", RepairApplicationController.class);
		this.add("/bonuspoints", BonusPointsController.class);
		this.add("/openphoneadv",OpenPhoneAdvController.class);
	}

}
