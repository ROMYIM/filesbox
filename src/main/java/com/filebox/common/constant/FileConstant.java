package com.filebox.common.constant;
/**
* @Description:TODO(信报箱常量)
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月11日
*/
public class FileConstant {
	/**
	 * 快递柜设备ID
	 */
	public static final String deviceId = "deviceId";
	public static final String fileDeviceId = "fileDeviceId";
	
	/**
	/**
	 * 设备信息缓存，拦截器用。
	 */
	public static final String apiDevice = "apiDevice";
	public static final String accessToken = "accessToken";
	public static final String ownerId = "ownerId";
	public static final int access_owner = 1;//api 归属人登陆
	public static final int access_courier = 2;//api 快递员登陆
	public static final int access_admin = 3;//api 管理员登陆
	
	public static final String apiAccountCache = "apiAccount";//api 登陆账号缓存
	public static final String loginAccount = "loginAccount";//登陆账号
	public static final String loginUser = "loginUser";//移动端登陆账号
	public static final String operator = "operator";//运营商
	
	public static final int code_701 = 701;//快递超时
	public static final int code_702 = 702;//快递员余额不足
	public static final int code_703 = 703;//快递员已存在
	public static final int code_704 = 704;//设备停用
	
	public static final int code_801 = 801;//归属人已存在
	public static final int code_802 = 802;//归属人信息修改成功
}
