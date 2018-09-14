package com.filebox.admin.filebox.sepcialuser;

import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
* @Description:TODO(添加特殊用户校验)
* @author 作者 : jinghui.su
* @date 创建时间：2017年7月4日
*/
public class AddOrModifySpecialUserValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequired("phone", "msg", "手机号不能为空");
		validateString("phone",11,11,"msg","请输入正确的手机号码");
		if (c.getHeader("deviceId")==null && c.getAttr(FileConstant.deviceId) == null) {
			addError("msg", "DeviceId不能为空");
		}
	}

	@Override
	protected void handleError(Controller c) {
		RetKit retKit = new RetKit();
		retKit.setFail(c.getAttr("msg"));
		c.renderJson(retKit);
	}

}
