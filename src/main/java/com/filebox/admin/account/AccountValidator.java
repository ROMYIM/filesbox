package com.filebox.admin.account;

import com.filebox.common.model.Account;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

/**
* @Description:TODO()
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月24日
*/
public class AccountValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequiredString("account.name", "msg", "姓名不能为空");
		validateString("account.phone", 11, 11, "msg", "手机格式不正确");
		validateRequiredString("account.role", "msg", "身份不能为空");
		
		String devices = c.getPara("devices");
		String identity = c.getPara("account.role");
		
		//新增
		if("save".equals(getActionMethod().getName())){
			if(identity.equals(Account.IDENTITY_NORMAL) && StrKit.isBlank(devices)){
				addError("msg", "请选择信报箱管理员要管理的信报箱");
			}
		}else if("update".equals(getActionMethod().getName())){
			if(identity.equals(Account.IDENTITY_NORMAL)&&StrKit.isBlank(devices)){
				addError("msg", "请选择信报箱管理员要管理的信报箱");
			}
		} else {
			addError("msg", "AccountValidator 只能用于 save、update 方法");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();
	}

}
