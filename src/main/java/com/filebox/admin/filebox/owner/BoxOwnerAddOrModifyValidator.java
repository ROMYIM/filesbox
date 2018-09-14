package com.filebox.admin.filebox.owner;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
* @Description:TODO(增加和修改箱子绑定人(一个箱子被一个人绑定))
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月24日
*/
public class BoxOwnerAddOrModifyValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequired("phone", "msg", "手机号不能为空");
		validateRequired("cabinet_num", "msg", "柜体编号不能为空");
		validateRequired("number", "msg", "箱子编号不能为空");
		if (c.getHeader("deviceId")==null) {
			addError("msg", "快递柜ID不能为空");
		}
	}

	@Override
	protected void handleError(Controller c) {
		RetKit retKit = new RetKit();
		retKit.setFail(c.getAttr("msg"));
		c.renderJson(retKit);
	}

}
