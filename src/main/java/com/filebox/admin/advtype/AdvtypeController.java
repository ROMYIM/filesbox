package com.filebox.admin.advtype;

import com.jfinal.core.Controller;

/**
* @Description:TODO(广告类型管理)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月24日
*/
public class AdvtypeController extends Controller {
	public static final AdvtypeService service  = AdvtypeService.me;
	
	public void index(){
		setAttr("list",service.advtypeList());
		render("index.html");
	}
	
	public void form(){
		if(getParaToInt() != null){
			setAttr("operator", service.getAdvTypeInfo(getParaToInt()));
		}
		render("form.html");
	}
	
}
