package com.filebox.admin.protocol;

import java.io.File;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Protocol;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

/**
* @Description:TODO(协议-百度富文本编辑器统一调用控制器)
* @author 作者 : jinghui.su
* @date 创建时间：2017年6月25日
*/
@Clear
public class ProtocolController extends Controller {
	
	public static final ProtocolService srv = ProtocolService.me;

	public void index(){
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		String name = getPara("name");
		if (StrKit.notBlank(name) && name.equals("null")) {
			name =null;
		}
		JMap para = JMap.create().set("name", name);
		keepPara();
		RetKit ret = srv.listByPage(ps, pn, para);
		setAttr("page", ret.get("page"));
		render("index.html");
	}
	
	public void form(){
		if (getParaToInt()!=null) {
			setAttr("protocol", srv.getProtocolInfo(getParaToInt()));
		}
		render("form.html");
	}
	
    /**
     * 添加协议
     */
    public void addProtocol(){
    	Protocol protocol = getModel(Protocol.class,"");
    	RetKit retKit = srv.addProtocol(protocol);
    	renderJson(retKit);
    }
    
    /**
     *修改协议
     */
    public void updateProtocol(){
    	Protocol protocol = getModel(Protocol.class,"");
    	RetKit retKit = srv.updateProtocol(protocol);
    	renderJson(retKit);
    }
    
    /**
     * 删除协议
     */
    public void delete(){
    	int protocolId = getParaToInt();
		RetKit retKit = srv.deleteProtocol(protocolId);
		renderJson(retKit);
    }
 
}
