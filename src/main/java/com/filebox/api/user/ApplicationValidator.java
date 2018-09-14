package com.filebox.api.user;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class ApplicationValidator extends Validator {

	public ApplicationValidator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		Integer id = c.getParaToInt("id");
		if (id == null) {
			addError("EMPTY_ID", "箱子Id不能为空");
		}
	}

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub
		c.renderJson(RetKit.fail(c.getAttr("EMPTY_ID")));
	}

}
