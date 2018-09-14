package com.filebox.api.protocol;

import com.filebox.admin.protocol.ProtocolService;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Protocol;

/**
* @Description:TODO()
* @author 作者 : jinghui.su
* @date 创建时间：2017年6月28日
*/
public class ApiPortocolService {
	public static final ApiPortocolService me = new ApiPortocolService();
	static final Protocol protocolDao = new Protocol().dao();
	/**
	 * 获取注册协议
	 */
	public RetKit getRegistProtocol(){
		RetKit retKit = new RetKit();
		Protocol protocol = protocolDao.findById(1);
		if(protocol != null){
			retKit.set("pro", protocol.getContent());
		}else{
			retKit.set("pro", "");
		}
		return retKit;
	}
}
