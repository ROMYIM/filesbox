package com.filebox.api.user;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class ApiUserLoginValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		setShortCircuit(true);
		validateRequired("phone", "msg", "手机号码不能为空");
		validateString("phone", 11, 11, "msg", "手机号码格式不对");
		validateRequired("password", "msg", "密码不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub
		RetKit ret = new RetKit();
		ret.setFail(c.getAttr("msg"));	
		c.renderJson(ret);
	}

}
