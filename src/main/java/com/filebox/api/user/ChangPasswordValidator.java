package com.filebox.api.user;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBoxOwner;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.validate.Validator;

public class ChangPasswordValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		setShortCircuit(true);
		validateString("password", 6,32,"msg", "密码长度不能少于6位");
		validateRequired("oldPassword","msg", "请输入旧密码");
		
		FileBoxOwner user=new FileBoxOwner().dao().findFirst("select * from file_box_owner where phone=? limit 1", c.getPara("phone"));
		if(user==null){
			addError("msg","用户不存在");
		}
		String pw=user.getPassword();
		if(!pw.equals(HashKit.md5(c.getPara("oldPassword")))){
			addError("msg","用户名或密码错误");
		}
		
		if(c.getPara("password").length()<6){
			addError("msg","密码长度不能小于6位");
		}
		else if(!c.getPara("password").equals(c.getPara("passwordConfirm"))){
			addError("msg","输入的新密码不一致");
		}
	}

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub
		RetKit ret=new RetKit();
		ret.setFail(c.getAttr("msg"));
		c.renderJson(ret);
	}

}
