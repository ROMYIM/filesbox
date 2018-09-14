package com.filebox.admin.login;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * ajax 登录参数验证
 */
public class LoginValidator extends Validator {

	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequired("phone", "msg", "邮箱不能为空");
		//validateEmail("userName", "userNameMsg", "邮箱格式不正确");
		validateRequired("password", "msg", "密码不能为空");
		//validateCaptcha("captcha", "captchaMsg", "验证码不正确");
	}

	protected void handleError(Controller c) {
		c.renderJson(RetKit.fail(c.getAttr("msg")));
	}
}
