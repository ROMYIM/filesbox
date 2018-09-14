package com.filebox.admin.device;

import java.util.List;

import com.filebox.admin.account.AccountService;
import com.filebox.admin.login.LoginService;
import com.filebox.common.model.Account;
import com.filebox.common.model.AccountDevice;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * @Description:TODO()
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月19日
 */
public class FileDeviceValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		Account loginAccount = c.getAttr(LoginService.loginAccountCacheName);
		// String deviceId = c.getPara("device.id");
		String deviceId = c.getPara("device.id", c.getPara());
		if (!validate(deviceId, loginAccount)) {
			addError("msg", "你无权限操作此设备");
		}
	}

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub

	}

	private boolean validate(String deviceId, Account loginAccount) {
		if (loginAccount.isSuperAdmin()) {
			return true;
		} else {
			List<AccountDevice> list = AccountService.getManagerBox(loginAccount.getId());
			for (AccountDevice accountDevice : list) {
				if (deviceId.equals(accountDevice.getDeviceId()))
					return true;
			}
		}
		return false;
	}

}
