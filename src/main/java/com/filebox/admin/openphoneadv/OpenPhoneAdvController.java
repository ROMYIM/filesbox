package com.filebox.admin.openphoneadv;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.AdvertisingPhone;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

public class OpenPhoneAdvController extends Controller {
	
	public static final OpenPhoneAdvService service = new OpenPhoneAdvService();
	
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
		render("openphoneadv_form.html");
	}
	
	public void edit(){
		setAttr("openphoneadv", service.edit(getPara()));
		render("openphoneadv_form.html");
	}
	
	public void update(){
		UploadFile uf = getFile("photo",service.getUploadPath(),service.getAvatarMaxSize());
		service.update(getModel(AdvertisingPhone.class), uf);
		renderJson(RetKit.ok());
	}
	
	public void save(){
		UploadFile uf = getFile("photo",service.getUploadPath(),service.getAvatarMaxSize());
		String text = getPara("radio");
		System.out.println(text+"++++++++++");
		if (uf == null) {
			renderJson(RetKit.fail("msg", "请选择上传的文件"));
			return;
		}
		RetKit ret = service.uploadAvatar(uf);
		if (ret.success()) {
			int sort = getParaToInt("advertisingPhone.sort");
			int operatorId = getParaToInt("advertisingPhone.operatorId");
			int adv_typeId = getParaToInt("advertisingPhone.adv_typeId");
			int adv_status = getParaToInt("advertisingPhone.adv_status");
			String textcontent = getPara("advertisingPhone.textcontent");
			String link = getPara("advertisingPhone.link");
			String address = ret.get("avatarUrl").toString();
			String informationlink = getPara("advertisingPhone.informationlink");
			service.save(sort,link,operatorId,adv_typeId,address,textcontent,adv_status,informationlink);
		}
		renderJson(ret);
	}
	
	public void savetext(){
		String textcontent = getPara("advertisingPhone.textcontent");
		service.savetext(textcontent);
	}
	
	public void delete(){
		renderJson(service.delete(getPara("id")));
	}
	
}
