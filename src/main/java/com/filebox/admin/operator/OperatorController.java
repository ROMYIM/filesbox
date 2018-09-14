package com.filebox.admin.operator;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Operator;
import com.jfinal.core.Controller;

/**
* @Description:TODO(运营商管理)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月24日
*/
public class OperatorController extends Controller {
	public static final OperatorService service  = OperatorService.me;
	
	public void index(){
		setAttr("list",service.operatorList());
		render("index.html");
	}
	
	public void form(){
		if(getParaToInt() != null){
			setAttr("operator", service.getOperatorInfo(getParaToInt()));
		}
		render("form.html");
	}
	
	public void addOperator(){
		Operator model = getModel(Operator.class,"");
		RetKit retKit =service.addOperator(model);
		renderJson(retKit);
	}
	
	public void updateOperator(){
		Operator model = getModel(Operator.class,"");
		RetKit retKit = service.updateOperator(model);
		renderJson(retKit);
	}
	
	public void delete(){
		RetKit retKit = service.delete(getParaToInt());
		renderJson(retKit);
	}
}
