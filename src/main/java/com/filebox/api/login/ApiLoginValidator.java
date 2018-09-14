package com.filebox.api.login;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
* @Description:TODO(登录参数验证)
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月24日
*/
public class ApiLoginValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequired("phone", "msg", "手机号不能为空");
		validateRequired("password", "msg", "密码不能为空");
		validateRequired("type", "msg", "类型不能为空");
		if (c.getHeader("deviceId")==null) {
			addError("msg", "信报箱ID不能为空");
		}
	}

	@Override
	protected void handleError(Controller c) {
		RetKit retKit = new RetKit();
		retKit.setFail(c.getAttr("msg"));
		c.renderJson(retKit);
	}

}
