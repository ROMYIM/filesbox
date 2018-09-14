package com.filebox.common.inteceptor;

import java.util.List;

import com.filebox.admin.account.AccountService;
import com.filebox.admin.device.FileDeviceService;
import com.filebox.admin.filebox.FileBoxService;
import com.filebox.admin.login.LoginService;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.model.Account;
import com.filebox.common.model.AccountDevice;
import com.filebox.common.model.BindApplication;
import com.filebox.common.model.RepairApplication;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

/**
 * @Description:TODO(后台拦截访问的)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月17日
 */
public class DeviceInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		Account loginAccount = controller.getAttr(LoginService.loginAccountCacheName);
		String cabinetId = FileDeviceService.me.getFiledeviceId(controller.getCookie(LoginService.sessionIdName));
		if(StrKit.notBlank(cabinetId)&&validate(cabinetId, loginAccount)){
			controller.setAttr(FileConstant.deviceId, cabinetId);
			controller.setAttr("count", BindApplication.getApplicationCount(cabinetId));
			controller.setAttr("repairCount", RepairApplication.getPendingCount(cabinetId));
			inv.invoke();
		}else{
			controller.redirect("/device");
		}
	}

	private boolean validate(String fileCabinetId, Account loginAccount) {
		if (loginAccount.isSuperAdmin()) {
			return true;
		} else {
			List<AccountDevice> list = AccountService.getManagerBox(loginAccount.getId());
			for (AccountDevice accountCabinet : list) {
				if (fileCabinetId.equals(accountCabinet.getDeviceId())) {
					return true;
				}
			}
		}
		return false;
	}

}
