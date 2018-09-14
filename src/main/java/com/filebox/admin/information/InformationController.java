package com.filebox.admin.information;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Information;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;


public class InformationController extends Controller {
	
	public static final InformationService service = new InformationService();
	
	public void index(){
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		String name = getPara("name");
		if (StrKit.notBlank(name) && name.equals("null")) {
			name =null;
		}
		JMap para = JMap.create().set("name", name);
		keepPara();
		RetKit ret = service.listByPage(ps, pn, para);
		//setAttr("articles", Information.me.find("select title from information "));
		setAttr("page", ret.get("page"));
		render("index.html");
	}
	
	public void add(){
		render("information_form.html");
	}
	
	public void delete(){
		renderJson(service.delete(getPara("id")));
	}
	
	public void edit(){
		setAttr("information", service.edit(getPara()));
		render("information_form.html");
	}
	
	
	
	public void save(){
		String content = getPara("content");
		String title = getPara("title");
		int sort = getParaToInt("information.sort");
		 //Information in= getModel(Information.class, "");
		System.out.println("+++"+content+title+sort);
	    service.save(content,title,sort);
		renderJson(RetKit.ok("msg",content));
	}
	
	public void update(){
		service.update(getModel(Information.class));
		renderJson(RetKit.ok());
	}

}
