package com.filebox.api.protocol;

import com.filebox.admin.protocol.ProtocolService;
import com.filebox.common.kit.RetKit;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

/**
* @Description:TODO(协议接口)
* @author 作者 : jinghui.su
* @date 创建时间：2017年6月28日
*/
public class ApiProtocolControl extends Controller {
	public static final ApiPortocolService srv = ApiPortocolService.me;
	   
    /**
     * 注册协议
     */
	@Clear
    public void getRegistProtocol(){
    	RetKit retKit = srv.getRegistProtocol();
    	String teString = retKit.get("pro").toString();
    	renderHtml(teString);
    }
    
}
