package com.filebox.api.common;

import java.util.Date;
import java.util.List;

import com.filebox.admin.account.AccountService;
import com.filebox.admin.device.FileDeviceService;
import com.filebox.common.kit.RandomKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileBoxOwner;
import com.filebox.common.model.FileCourier;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.Operator;
import com.filebox.common.model.PickupRecord;
import com.filebox.utils.CytSdkUtil;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.template.ext.directive.Random;

/**
 * @Description:TODO(公共服务)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月20日
 */
public class CommonService {
	public static final CommonService me = new CommonService();
	public static final FileBoxOwner fileBoxOwnerDao = new FileBoxOwner();
	public static final Account accountDao = new Account();

	/**
	 * 根据信报箱ID获取运营商信息
	 */
	public Operator getOperator(String deviceId) {
		return new Operator().findFirst(
				"select b.* from file_device a,operator b where a.id = ? and a.operator_id = b.id ", deviceId);
	}

	
	/**
	 * 获取banner
	 */
	public RetKit getBanner(String deviceId){
		FileDevice device = FileDeviceService.me.getDevice(deviceId);
		if(device==null || device.getOperatorId() == 0){
			return RetKit.ok("data",null);
		}
		List<Record> list = Db.find("select * from advertising where operatorId = ?",device.getOperatorId());
		return RetKit.ok("data", list);
	}
	
	
	
	/**
	 * 保存取件记录
	 */
	public boolean savePickUpRecord(String phone, int fileId, int identity) {
		PickupRecord pickupRecord = new PickupRecord();
		pickupRecord.setFileId(fileId);
		pickupRecord.setPickupIdentity(identity);
		pickupRecord.setPickupPhone(phone);
		pickupRecord.setPickupTime(new Date());
		return pickupRecord.save();
	}

	/**
	 * 更改信报箱状态
	 */
	public boolean updateFileBoxStatus(String deviceId, int cabinetNum, int boxNumber, int boxStatus) {
		FileDevice device = getDeviceInfo(deviceId);
		if (device.isDisable()) {
			boxStatus = FileBox.BOX_STATUS_STOP;
		}
		int count = Db.update(
				"update file_box set box_status = ?" + " where device_id=? and cabinet_num =? and number = ?",
				boxStatus, deviceId, cabinetNum, boxNumber);
		return count > 0;
	}

	/**
	 * 获取快递柜信息
	 * 
	 * @return
	 */
	public FileDevice getDeviceInfo(String deviceId) {
		return new FileDevice().findById(deviceId);
	}

	/**
	 * 更新设备使用率
	 */
	public boolean updateUseRate(String deviceId) {
		Record totalRecord = Db.findFirst("select count(*) as totalRow from file_box where device_id = ?", deviceId);
		Record record = Db.findFirst(
				"select count(*) as useCount from file_box " + "where device_id = ? and box_status = ?", deviceId,
				FileBox.BOX_STATUS_USED);
		Long tolalCount = totalRecord.get("totalRow");
		Long useCount = record.getLong("useCount");
		float tf = Float.valueOf(useCount) / Float.valueOf(tolalCount);
		tf = (float) (Math.round(tf * 100)) / 100;
		int count = Db.update("update file_device set usage_rate = ? where id = ?", String.valueOf(tf), deviceId);
		return count > 0 ? true : false;
	}

	/**
	 * 发送验证码
	 */
	public RetKit sendForgetPwdCode(String phone) {
		if (phone == null) {
			return RetKit.fail("手机号不能为空");
		}
		String verifyCode = RandomKit.getRandomPsw(4);
		String msg = "验证码：" + verifyCode + "，请尽快输入，如非本人操作，请不要理会。";
		System.out.println(msg);
		CacheKit.put(AccountService.verifyCodeCach, phone, verifyCode);
		return CytSdkUtil.sendMessage(phone, msg);
	}

	/**
	 * 忘记密码-验证码验证
	 */
	public RetKit updatePassword(String role, String phone, String password, String vertifyCode) {
		String cachCode = CacheKit.get(AccountService.verifyCodeCach, phone);
		if (StrKit.isBlank(role)) {
			return RetKit.fail("角色不能为空");
		}
		if (StrKit.isBlank(phone) || StrKit.isBlank(password)) {
			return RetKit.fail("手机号或密码不能为空");
		}
		if (cachCode == null) {
			return RetKit.fail("验证码不存在,请重新发送");
		}
		if (!cachCode.equals(vertifyCode)) {
			return RetKit.fail("验证码错误");
		}
		int count = 0;
		if (role.equals(ForgetPasswordRole.ROLE_BOX_OWNER)) { // 箱子用户
			count = Db.update("update file_box_owner set password = ? where phone = ?", password, phone);
			if (count > 0) {
				CacheKit.remove(AccountService.verifyCodeCach, phone);// 删除验证码缓存
				return RetKit.ok();
			} else {
				return RetKit.fail();
			}
		} else if (role.equals(ForgetPasswordRole.ROLE_COURIER)) { // 快递员
			count = Db.update("update file_courier set password = ? where phone = ?", password, phone);
			if (count > 0) {
				CacheKit.remove(AccountService.verifyCodeCach, phone);// 删除验证码缓存
				return RetKit.ok();
			} else {
				return RetKit.fail();
			}
		} else {
			count = Db.update("update account set password = ? where phone = ?", password, phone);
			if (count > 0) {
				CacheKit.remove(AccountService.verifyCodeCach, phone);// 删除验证码缓存
				return RetKit.ok();
			} else {
				return RetKit.fail();
			}
		}
	}

	/**
	 * 修改密码
	 */
	public RetKit changePassword(String role, String phone, String oldPassword, String newPassword) {
		if (StrKit.isBlank(role)) {
			return RetKit.fail("角色不能为空");
		}
		if (StrKit.isBlank(phone)) {
			return RetKit.fail("手机号不能为空");
		}
		if (StrKit.isBlank(oldPassword)) {
			return RetKit.fail("旧密码不能为空");
		}
		if (StrKit.isBlank(newPassword)) {
			return RetKit.fail("新密码不能为空");
		}

		int count = 0;
		if (role.equals(ForgetPasswordRole.ROLE_BOX_OWNER)) { // 箱子用户
			FileBoxOwner fileBoxOwner = fileBoxOwnerDao
					.findFirst("select * from file_box_owner where phone = ? and password = ?", phone, oldPassword);
			if (fileBoxOwner == null) {
				return RetKit.fail("用户名或密码有误");
			} else {
				count = Db.update("update file_box_owner set password = ? where phone = ?", newPassword, phone);
				return count > 0 ? RetKit.ok() : RetKit.fail();
			}

		} else if (role.equals(ForgetPasswordRole.ROLE_COURIER)) { // 快递员
			// 暂未提供
			return  RetKit.fail("暂未支持修改快递员密码");
		} else {
			Account account = accountDao.findFirst("select * from account where phone = ? and password = ?", phone,
					oldPassword);
			if (account == null) {
				return RetKit.fail("用户名或密码有误");
			} else {
				count = Db.update("update account set password = ? where phone = ?", newPassword, phone);
				return count > 0 ? RetKit.ok() : RetKit.fail();
			}
		}
	}
}
