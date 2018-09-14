package com.filebox.admin.account;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
* @Description:TODO(忘记密码信息验证)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月19日
*/
public class UpdateValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("account.id", "msg", "id不能为空");
		validateRequiredString("account.phone", "msg", "手机号不能为空");
		validateRequiredString("account.password","msg", "新密码不能为空");
		validateRequiredString("password2","msg", "新密码不能为空");
		validateRequiredString("verifyCode","msg", "验证码不能为空");
		if (!c.getPara("account.password","").equals(c.getPara("password2",""))) {
			addError("msg", "两次密码不一致");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(RetKit.fail(c.getAttr("msg")));
	}

}
