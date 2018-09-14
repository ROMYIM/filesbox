 package com.filebox.api.user;


import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBoxOwner;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class SendVerifyCodeVaildator extends Validator {
// type类型：1为注册，2为找回密码
	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		setShortCircuit(true);
		validateRequired("phone", "msg", "手机号码不能为空");
		validateRequired("type", "msg", "type不能为空");
		validateString("phone", 11, 11, "msg", "手机号码格式不对！");
		FileBoxOwner user=new FileBoxOwner().dao().findFirst("select * from file_box_owner where phone=? limit 1", c.getPara("phone"));
		if(user!=null&&c.getPara("type").equals("1")){
			addError("msg", "该号码已经注册！");
		}
		if(user==null&&c.getPara("type").equals("2")){
			addError("msg", "该号码尚未注册！");
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
