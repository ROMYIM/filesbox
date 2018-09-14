package com.filebox.admin.model;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Model;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;

/**
* @Description:TODO(设备模型)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月24日
*/
public class ModelController extends Controller {
	public static final ModelService service  = ModelService.me;
	
//	public void index(){
//		setAttr("list", service.modelList());
//		render("index.html");
//	}
	
	public void index() {
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		String name = getPara("name");
		if (StrKit.notBlank(name) && name.equals("null")) {
			name =null;
		}
		JMap para = JMap.create().set("name", name);
		keepPara();
		RetKit ret = service.listByPage(ps, pn, para);
		setAttr("page", ret.get("page"));
		render("index.html");
	}
	
	public void form(){
		if(getParaToInt() != null){
			setAttr("model", service.getModelInfo(getParaToInt()));
			setAttr("modelDetail", service.getModelDetail(getParaToInt()));
		}
		render("form.html");
	}
	
	/**
	 * 添加model
	 */
	@Before(ModelValidator.class)
	public void addModel(){
		Integer[] boxSpecs = getParaValuesToInt("boxSpec");
		Integer[] boxfloor = getParaValuesToInt("boxFloor");
 		Model model = getModel(Model.class);
		RetKit ret = service.addModel(model,boxSpecs,boxfloor);
		renderJson(ret);
	}
	
	public void updateModel(){
		Integer[] boxSpecs = getParaValuesToInt("boxSpec");
		Integer[] boxfloor = getParaValuesToInt("boxFloor");
		Model model = getModel(Model.class);
		RetKit ret =  service.updateModel(model,boxSpecs,boxfloor);
		renderJson(ret);
	}
	
	public void delete(){
		int id = getParaToInt();
		boolean success =service.delete(id);
		renderJson(success?RetKit.ok():RetKit.fail());
	}
	
}
