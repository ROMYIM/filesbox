package com.filebox.admin.adv;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Advertising;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

/**
* @Description:TODO(广告管理)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月25日
*/
public class AdvertisingController extends Controller {
public static final AdvertisingService service = AdvertisingService.me;
	
//	public void index(){
//		setAttr("list", service.list());
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

	
	
	public void add(){
		render("adv_form.html");
	}
	
	public void edit(){
		setAttr("adv", service.edit(getPara()));
		render("adv_form.html");
	}
	
	public void update(){
		UploadFile uf = getFile("photo",service.getUploadPath(),service.getAvatarMaxSize());
		service.update(getModel(Advertising.class),uf);
		renderJson(RetKit.ok());
	}
	
	public void save(){
		UploadFile uf = getFile("photo",service.getUploadPath(),service.getAvatarMaxSize());
		if (uf == null) {
			renderJson(RetKit.fail("msg", "请选择上传的文件"));
			return;
		}
		RetKit ret = service.uploadAvatar(uf);
		if (ret.success()) {
			int sort = getParaToInt("advertising.sort");
			int operatorId = getParaToInt("advertising.operatorId");
			String address = ret.get("avatarUrl").toString();
			service.save(sort,operatorId,address);
		}
		renderJson(ret);
	}
	
	public void delete(){
		renderJson(service.delete(getPara("id")));
	}
	
	
}
