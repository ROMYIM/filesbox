package com.filebox.api.user;

import com.filebox.api.user.SendVerifyCodeVaildator;
import com.filebox.api.user.ChangPasswordValidator;

import java.util.List;

import com.filebox.common.constant.FileConstant;
import com.filebox.admin.filebox.FileBoxService;
import com.filebox.admin.filebox.owner.FileBoxOwnerService;
import com.filebox.api.login.ApiLoginService;
import com.filebox.api.user.ApiUserLoginValidator;
import com.filebox.api.user.RegisterAndFindValidator;
import com.filebox.common.kit.RetKit;
import com.filebox.common.inteceptor.ApiUserAccessTokenIntercepter;
import com.filebox.common.model.BonusPoints;
import com.filebox.common.model.File;
import com.filebox.common.model.RepairApplication;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.json.JFinalJson;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.kit.IpKit;


/**
 * @Description:TODO(user控制器)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月17日
 */
@Clear
public class ApiUserController extends Controller {
	public static final ApiUserService srv = ApiUserService.me;

	/*
	 * 获取用户指定设备所拥有的锁列表
	 */
	public void pickup() {
		String phone = getPara("phone");
		int pickUpWay = 0;
		if (getPara("pickUpWay") != null) {
			pickUpWay = Integer.parseInt(getPara("pickUpWay"));
		}
		String wayCode = getPara("wayCode");
		String deviceId = getHeader(FileConstant.deviceId);
		String ip = IpKit.getRealIp(getRequest());
		if(pickUpWay == File.TAKE_WYAY_3){ //注意短信获取没有判断用户是否被禁用
			renderJson(srv.pickupByCode(deviceId, phone, pickUpWay, wayCode, ip));
		}else{
			renderJson(srv.pickupOtherWay(deviceId, phone, pickUpWay, wayCode, ip));
		}
		
	}
	// 登录
	@Clear
	@Before(ApiUserLoginValidator.class)
	public void login() {
		String phone = getPara("phone");
		String password = getPara("password");
		String client_type=getPara("client_type");
		RetKit ret = srv.doLogin(phone, password,client_type);
		renderJson(ret);
	}
	
	// 注册
	@Clear
	@Before(RegisterAndFindValidator.class)
	public void regist() {
		String phone = getPara("phone");
		String password = getPara("password");
		RetKit rt = srv.regist(phone, password);
		renderJson(rt);
	}
	
	// 发送验证码及校验
	@Clear
	@Before(SendVerifyCodeVaildator.class)
	public void sendVerifyCode() {
		String phone = getPara("phone");
		RetKit ret = srv.sendVerifyCode(phone);
		renderJson(ret);
	}
	
	// 找回密码
	@Clear
	@Before(RegisterAndFindValidator.class)
	public void findPassword() {
		String phone = getPara("phone");
		String password = getPara("password");
		RetKit ret = srv.updatePassWord(phone, password);
		renderJson(ret);
	}

	// 发送验证码前设置type=2，修改密码
	
	@Clear
	@Before(ChangPasswordValidator.class)
	public void changePassword() {
		RetKit ret;
		String phone = getPara("phone");
		String password = getPara("password");
		ret = srv.changePassword(phone, password);
		renderJson(ret);
	}


	/**
	 * 信报箱归属人所含设备箱子列表
	 */
	@Clear
	public void ownerDeviceBoxList() {
		String accessToken = getHeader(FileConstant.accessToken);
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer ownerId = loginUser.getInt("id");
		String deviceId = getPara("deviceId");
		List<Record> list = FileBoxOwnerService.me.ownerDeviceList(ownerId.toString(), deviceId);
		renderJson(RetKit.ok("data", list));
	}
	
	
	/**
	 * 用户所拥有的信报箱列表
	 */
	@Clear
	@Before(ApiUserAccessTokenIntercepter.class)
	public void ownerDevice(){
		String accessToken = getHeader(FileConstant.accessToken);
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer ownerId = loginUser.getInt("id");
		List<Record> list = FileBoxOwnerService.me.ownerDevice(ownerId);
		renderJson(RetKit.ok("data", list));
	}
	
	/**
	 * 用户初次绑定时返回所有设备列表
	 * 供用户选择
	 */
	@Before(ApiUserAccessTokenIntercepter.class)
	public void selectDevice() {
		List<Record> list = srv.selectDevice();
		renderJson(RetKit.ok("data", list));
	}
	
	/**
	 * 用户初次绑定时返回用户所选设备中
	 * 其他用户还没绑定的信报箱
	 * 用户必须已经选择设备
	 */
	@Before(ApiUserAccessTokenIntercepter.class)
	public void selectBox() {
		String deviceId = getPara("deviceId");
		List<Record> list = srv.selectBox(deviceId);
		renderJson(RetKit.ok("data", list));
	}
	
	/**
	 * 适用板子开锁
	 */
	@Clear
	public void openFileBox(){
		String deviceId = getHeader(FileConstant.deviceId);
		int cabinetNum = getParaToInt("cabinetNum", 0);
		int number = getParaToInt("number",0);
		RetKit ret = FileBoxService.me.openFileBox(deviceId, cabinetNum, number);	
		renderJson(ret);
	}
	
	/**
	 * 手机开锁
	 */
	@Before(ApiUserAccessTokenIntercepter.class)
	public void userOpenBox() {
		String deviceId = getPara("deviceId");
		int cabinetNum = getParaToInt("cabinetNum", 0);
		int number = getParaToInt("number",0);
		String accessToken = getHeader(FileConstant.accessToken);
		RetKit ret = FileBoxService.me.openFileBox(accessToken, deviceId, cabinetNum, number);
		if (ret.success()) {
			ret = srv.bonusPoints(accessToken, BonusPoints.OPEN_DOOR_POINT);
		}
		renderJson(ret);
	}
	
	/**
	 * 申请绑定。数据会录到申请绑定的数据库中。
	 * 之后管理员会再根据该数据库是否同意，再把数据录到owner_device和owner_detail中
	 */
	@Before({ApiUserAccessTokenIntercepter.class, ApplicationValidator.class})
	public void bindBox() {
		String accessToken = getHeader(FileConstant.accessToken);
		renderJson(srv.bindBox(accessToken, getParaToInt("id")));
	}
	
	/**
	 * 申请解绑。成功后等待管理员操作
	 */
	@Before({ApiUserAccessTokenIntercepter.class, ApplicationValidator.class})
	public void unBindBox() {
		Integer boxId = getParaToInt("id");
		String accessToken = getHeader(FileConstant.accessToken);
		renderJson(srv.unBindBox(boxId, accessToken));
	}
	
	
	@Before(ApiUserAccessTokenIntercepter.class)
	public void repairApplication() {
		UploadFile uploadPicture = getFile("photo");
		Integer boxId = getParaToInt("id");
		String accessToken = getHeader(FileConstant.accessToken);
		renderJson(srv.repairApplication(accessToken, boxId, uploadPicture, getParaToInt("type"), getPara("repair_detail")));
	}
	
	@Before(ApiUserAccessTokenIntercepter.class)
	public void bonusPointsRecord() {
		String accessToken = getHeader(FileConstant.accessToken);
		RetKit retKit = srv.bonusPointsRecord(accessToken);
		renderJson(retKit);
	}
	
	@Before(ApiUserAccessTokenIntercepter.class)
	public void reducePointsTest() {
		String accessToken = getHeader(FileConstant.accessToken);
		renderJson(srv.bonusPoints(accessToken, BonusPoints.OPEN_DOOR_REDUCE));
	}
}
