package com.filebox.utils;

import com.filebox.common.kit.RetKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.smsweb.www.servlet.anna.AnnaHttpSendResponseObject;
import com.smsweb.www.servlet.anna.SDKProxy;

/**
* @Description:TODO(维纳多短信接口)
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月26日
*/
public class CytSdkUtil {
	
	private static Prop p = PropKit.use("config.properties");
	private final static String SEND_SUCC = "0";
	
	public static RetKit sendMessage(String phone,String msg){
		SDKProxy sdkProxy = new SDKProxy();
		sdkProxy.init("http://yl.mobsms.net/send/sendAnna.aspx?",p.get("msgAccount") , p.get("msgPassword"));
		AnnaHttpSendResponseObject oneresp = sdkProxy.send(phone, msg);
		System.out.println("------------"+oneresp.getErrid());
		System.out.println("------------"+oneresp.getErr());
		if (oneresp.getErrid().equals(SEND_SUCC)) {
			return RetKit.ok("发送成功");
		}
		return RetKit.fail(getCode(Integer.parseInt(oneresp.getErrid())));
	}
	
	public static String getCode(int Code){
		switch (Code) {
		case 6002:
			return "用户帐号不正确";
		case 6008:
			return "无效的手机号码";
		case 6009:
			return "手机号码是黑名单";
		case 6010:
			return "用户密码不正确";
		case 6011:
			return "短信内容超过了最大长度限制";
		case 6012:
			return "该企业用户设置了ip限制 ";
		case 6013:
			return "该企业用户余额不足";
		case 6014:
			return "发送短信内容不能为空";
		case 6015:
			return "发送内容中含非法字符";
		case 6019:
			return "账户已停机，请联系客服";
		case 6021:
			return "扩展号码未备案";
		case 6023:
			return "发送手机号码超过太长，已超过300个号码";
		case 6024:
			return "定制时间不正确";
		case 6025:
			return "扩展号码太长（总长度超过20位） ";
		case 6080:
			return "提交异常，请联系服务商解决 ";
		case 6085:
			return "短信内容为空";
		default:
			return "发送成功";
		}
	}
	
	public static void main(String[] args) {
		System.out.println(sendMessage("13750002530", "测试"));
	}
}
