package com.filebox.admin.model;

import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
* @Description:TODO()
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月25日
*/
public class ModelValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequired("model.name", "msg", "型号名称不能为空");
		validateRequired("model.code", "msg", "型号代码不能为空");
		validateRequired("model.box_amount", "msg", "型号箱子个数不能为空");
		validateInteger("model.box_amount", 1, 100, "msg", "型号箱子个数有误");
		validateRequired("boxSpec", "msg", "箱子详情不能为空");
		int cabinetNum = c.getParaToInt("model.box_amount");
		String[] boxSpec = c.getParaValues("boxSpec");
		if (cabinetNum!=boxSpec.length) {
			addError("msg", "箱子个数不匹配");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(RetKit.fail(c.getAttr("msg")));
	}

}
