package com.filebox.api.user;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBoxOwner;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.validate.Validator;

public class RegisterAndFindValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		setShortCircuit(true);
		validateRequired("phone", "msg", "手机号码不能为空");
		validateString("phone",11,11,"msg","手机号码格式不对");
		validateRequired("password", "msg", "密码不能为空");
		validateRequired("passwordConfirm", "msg", "确认密码不能为空");
		validateRequired("verifyCode", "msg", "验证码不能为空");
	
		if(c.getPara("password").length()<6){
			addError("msg","密码长度不能少于6位");
		}else if(!c.getPara("password").equals(c.getPara("passwordConfirm"))){
			addError("msg","输入的密码不一致");
		}
		else if(!c.getPara("verifyCode").equals(CacheKit.get(ApiUserService.verifyCodeCache, c.getPara("phone")))){
			addError("msg","验证码错误");
		}
	}

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub
		RetKit ret = new RetKit();
		ret.setFail(c.getAttr("msg"));	
		c.renderJson(ret);
	}

}
