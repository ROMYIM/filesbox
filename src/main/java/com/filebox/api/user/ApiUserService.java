package com.filebox.api.user;

import java.io.DataInputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import org.eclipse.jetty.io.ByteArrayBuffer.CaseInsensitive;

import com.filebox.common.model.FileBoxOwner;
import com.filebox.utils.CytSdkUtil;
import com.filebox.common.kit.RandomKit;
import com.filebox.common.model.OwnerToken;
import com.filebox.common.constant.FileConstant;
import com.filebox.api.common.CommonService;
import com.filebox.api.login.ApiLoginService;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.BindApplication;
import com.filebox.common.model.BonusPoints;
import com.filebox.common.model.File;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.OwnerDetail;
import com.filebox.common.model.OwnerDevice;
import com.filebox.common.model.PickupRecord;
import com.filebox.common.model.RepairApplication;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
/**
* @Description:TODO(用户业务逻辑层)
* @author 作者 : jinghui.su
* @date 创建时间：2017年4月17日
*/
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;

public class ApiUserService {
	public static final ApiUserService me = new ApiUserService();
	static final File fileDao = new File().dao();
	static final PickupRecord pickupDao = new PickupRecord().dao();
	static final OwnerDevice ownerDeviceDao = new OwnerDevice().dao();
	static final FileBoxOwner fileBoxOwnerDao = new FileBoxOwner().dao();
	static final OwnerDetail ownerDetailDao = new OwnerDetail().dao();
	private static final FileBoxOwner dao = new FileBoxOwner().dao();
	static final BindApplication bindApplicationDao = new BindApplication().dao();
	static final FileBox fileBoxDao = new FileBox().dao();
	static final RepairApplication repairApplicatonDao = new RepairApplication().dao();
	static final BonusPoints bonusPointsDao = new BonusPoints().dao();
	public static final String verifyCodeCache = "verifyCodeCache";
	/**
	 * File.TAKE_WYAY_0 密码取件 ;1 card ; 2 指纹 ; 3 验证码
	 */

	public RetKit pickupByCode(final String deviceId, final String phone, int pickUpWay, String code, String ip) {
		final File file = fileDao.findFirst("select a.*,b.password,b.password_card,b.password_fingerprint from file a "
				+ "inner join file_box_owner b on a.owner_id = b.id"
				+ " and a.status = ? and a.pickup_code = ? order by save_time desc", File.STATUS_TAKE_IN, code);

		if (file == null) {
			return RetKit.fail("手机号或提取码错误");
		}

		boolean succ = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				boolean succ1 = CommonService.me.savePickUpRecord(phone, file.getId(), PickupRecord.PICKUP_USER);
				// 更改文件状态为取出
				file.setStatus(File.STATUS_TAKE_OUT);
				boolean succ2 = file.update();
				// 更改信报箱状态为待使用
				CommonService.me.updateFileBoxStatus(file.getDeviceId(), file.getCabinetNum(), file.getBoxNumber(),
						FileBox.BOX_STATUS_WAIT);

				return succ1 && succ2;
			}
		});

		// 免费取件
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("cabinet_num", file.getCabinetNum());
		map.put("box_number", file.getBoxNumber());

		// 更新使用率
		boolean succ4 = CommonService.me.updateUseRate(file.getDeviceId());
		return succ && succ4 ? RetKit.ok("data", map) : RetKit.fail("数据库异常，保存失败");

	}

	public RetKit pickupOtherWay(final String deviceId, final String phone, int pickUpWay, String pwd, String ip) {
		/**
		 * File.TAKE_WYAY_0 密码取件 ;1 card ; 2 指纹 ; 3 验证码
		 */
		final List<OwnerDevice> ownerDeviceList;
		OwnerDetail ownerDetail = null;
		FileBoxOwner owner = null;

		if (pickUpWay == File.TAKE_WYAY_0) {
			ownerDeviceList = ownerDeviceDao.find(
					"select b.* from file_box_owner a inner join owner_device b where"
							+ " b.device_id = ? and a.id = b.owner_id and a.phone = ? and a.password = ? and a.status = ? ",
					deviceId, phone, pwd, FileBoxOwner.STATUS_OK);
			owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where phone = ? and password = ?", phone,
					pwd);
			if(owner != null){
				ownerDetail = ownerDetailDao
						.findFirst("select * from owner_detail where device_id = ? and owner_id = ?", deviceId, owner.getId());
			}
		} else if (pickUpWay == File.TAKE_WYAY_1) {
			ownerDeviceList = ownerDeviceDao.find("select b.* from owner_detail a inner join owner_device b where"
					+ " b.device_id = ? and a.owner_id = b.owner_id and a.password_card = ?", deviceId, pwd);

			ownerDetail = ownerDetailDao
					.findFirst("select * from owner_detail where device_id = ? and password_card = ?", deviceId, pwd);
			if (ownerDetail != null) {
				owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where id = ?",
						ownerDetail.getOwnerId());
			}

		} else if (pickUpWay == File.TAKE_WYAY_2) {
			ownerDeviceList = ownerDeviceDao.find("select b.* from owner_detail a inner join owner_device b where"
					+ " b.device_id = ? and a.owner_id = b.owner_id and a.password_fingerprint = ?", deviceId, pwd);
			ownerDetail = ownerDetailDao
					.findFirst("select * from owner_detail where device_id = ? and password_fingerprint = ?", deviceId, pwd);
			if (ownerDetail != null) {
				owner = fileBoxOwnerDao.findFirst("select * from file_box_owner where id = ?",
						ownerDetail.getOwnerId());
				if (owner != null && owner.getStatus().equals(FileBoxOwner.STATUS_LOCK)) {

				}
			}

		} else {
			ownerDeviceList = null;
		}

		if (owner == null) {
			if (pickUpWay == File.TAKE_WYAY_0) {
				return RetKit.fail("手机号或密码错误");
			} else if (pickUpWay == File.TAKE_WYAY_1) {
				return RetKit.fail("该卡不存在");
			} else if (pickUpWay == File.TAKE_WYAY_2) {
				return RetKit.fail("指纹不存在");
			} else {
				return RetKit.fail("用户信息不存在");
			}
		} else if (owner != null && ownerDeviceList == null) {
			return RetKit.fail("用户信息不存在");
		} else if (owner != null && ownerDetail != null && ownerDetail.getOwnerDeviceStatus().equals(OwnerDetail.STATUS_LOCK)) {
			return RetKit.fail("用户已被禁用");
		}

		List<HashMap<String, Object>> hashMaps = new ArrayList<>();
		for (OwnerDevice ownerDevice : ownerDeviceList) {
			// 免费取件
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("cabinet_num", ownerDevice.getCabinetNum());
			map.put("number", ownerDevice.getNumber());
			hashMaps.add(map);
		}

		// 更新使用率
		boolean succ = CommonService.me.updateUseRate(deviceId);
		return succ ? RetKit.ok("data", hashMaps) : RetKit.fail("数据库异常，保存失败");

	}
	
	public RetKit doLogin(String phone, String password, String client_type) {
		FileBoxOwner user = dao.findFirst("select * from file_box_owner where phone=?limit 1", phone);
		RetKit ret = new RetKit();
		if (user == null) {
			return RetKit.fail("该户名不存在");
		} else if (!user.getPassword().equals(password)) {
			return RetKit.fail("你输入的密码不正确:" + password);
		}
		
		String accessToken = StrKit.getRandomUUID();
		if (!createToken(user.getId(), accessToken, client_type)) {
			return ret.setFail("保存token到数据库失败");
		}
		Map<String, Object> accessTokenMap = new HashMap<String, Object>();
		accessTokenMap.put(FileConstant.accessToken, accessToken);
		ret.set("data", accessTokenMap);
		Record record = user.toRecord();
		record.set(FileConstant.accessToken, accessToken);
		CacheKit.put(FileConstant.apiAccountCache, accessToken, record);
		return ret.setOk("登录成功");
	}
	
	public RetKit regist(String phone, String password) {
		String pw = HashKit.md5(password);
		int n = Db.update("insert into file_box_owner (phone,password) values (?,?)", phone, pw);
		if (n == 1) {
			CytSdkUtil.sendMessage(phone, "您已注册为快递柜客户！");
		}
		return RetKit.ok("注册成功！");
	}
	
	public RetKit sendVerifyCode(String phone) {

		String verifyCode = CacheKit.get(verifyCodeCache, phone);
		if (verifyCode != null) {
			return RetKit.fail("请不要重复发送验证码！");
		}
		String code = RandomKit.getRandomPsw(4);
		System.out.println("---------验证码是--------" + code);
//		RetKit ret = CytSdkUtil.sendMessage(phone, "验证码：" + code + "，您正在注册，如非本人操作，请不要理会");
		RetKit ret  = RetKit.ok();
		if (ret.success()) {
			CacheKit.put(verifyCodeCache, phone, code);
		}
		return ret;

	}
	public RetKit updatePassWord(String phone, String password) {
		FileBoxOwner user = new FileBoxOwner().dao().findFirst("select * from file_box_owner where phone=?", phone);
		if (user == null) {
			return RetKit.fail("密码重置失败！");

		}
		user.setPassword(HashKit.md5(password));
		if (user.update()) {
			return RetKit.ok("密码重置成功！");
		}
		return RetKit.fail("密码重置失败！");
	}

	public RetKit changePassword(String phone, String password) {
		String pw = HashKit.md5(password);
		int n = Db.update("update file_box_owner set password=? where phone=?", pw, phone);
		if (n < 0) {
			return RetKit.fail("修改密码失败");
		}
		return RetKit.ok("密码修改成功");
	}
	
	private boolean createToken(int ownerId, String token, String client_type) {
		Record record = Db.findFirst("select * from owner_token where owner_id = ?  ", ownerId);
		// 用于设置 登录时间点，转换成毫秒
		long loginTime = System.currentTimeMillis();
		if (record != null) {
			int count = Db.update("update owner_token set token = ?  where owner_id = ? ", token, ownerId);
			return count > 0;
		} else {
			OwnerToken ownertoken = new OwnerToken();
			ownertoken.setOwnerId(ownerId);
			ownertoken.setToken(token);
			ownertoken.setLoginTime(loginTime);
			ownertoken.setClientType(client_type);
			return ownertoken.save();
		}
	}
	
	/**
	 * 返回设备列表（即那三个设备）
	 * @return
	 */
	public List<Record> selectDevice() {
		List<Record> deviceList = Db.find("select id, name, address, cabinet_amount, status from file_device");
		return deviceList;
	}
	
	/**
	 * 在查询用户设备(owern_device)无结果时，即该用户没有绑定信报箱
	 * 查询file_box中当前用户所选择的设备中没有被其他用户绑定的信报箱
	 * @param ownerId
	 * @param deviceId
	 * @return 查询结果
	 */
	public List<Record> selectBox(String deviceId) {
		List<Record> fBoxList = Db.find("select distinct id, device_id, cabinet_num, number from file_box where device_id = ?"
				+ " and (select count(1) as num from owner_device where owner_device.device_id = file_box.device_id"
				+ " and owner_device.cabinet_num = file_box.cabinet_num and owner_device.number = file_box.number) = 0", deviceId);
		return fBoxList;
	}
	
	/**
	 * 用户选中箱子后发出绑定申请
	 * 申请信息会存入中间缓存表bind_application中，交由管理员操作
	 * @param accessToken
	 * @param boxId
	 * @return返回申请结果的成功或失败信息
	 */
	public RetKit bindBox(String accessToken, Integer boxId) {
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer ownerId = loginUser.getInt("id");
		FileBox tempFileBox = fileBoxDao.findById(boxId);
		switch (tempFileBox.getBoxStatus()) {
		case FileBox.BOX_STATUS_DELETE: return  RetKit.fail("信报箱已被删除");
		case FileBox.BOX_STATUS_REPAIR: return RetKit.fail("信报箱维修中");
		case FileBox.BOX_STATUS_STOP: return RetKit.fail("信报箱已停止使用");
		case FileBox.BOX_STATUS_USED: return RetKit.fail("信报箱正在使用中");
		}
		if (tempFileBox.getCode() != FileBox.CODE_0) {
			return new RetKit().setFail("箱子不能正常使用");
		}
		if (ownerDeviceDao.findFirst("select * from owner_device where device_id = ? and cabinet_num = ? and number = ?", tempFileBox.getDeviceId(), tempFileBox.getCabinetNum(), tempFileBox.getNumber()) != null) {
			return new RetKit().setFail("箱子已被绑定");
		}
		if (bindApplicationDao.findFirst("select * from bind_application where owner_id = ? and box_id = ? and code = ?", ownerId, boxId, BindApplication.BIND_CODE) != null) {
			return RetKit.fail("已发出过申请，请耐心等待管理员操作");
		}
		
		Date timeStamp = BindApplication.getCurrentTimeStamp();
		String phone = fileBoxOwnerDao.findById(ownerId).getPhone();
		new BindApplication().setOwnerId(ownerId).setPhone(phone).setBoxId(boxId).setDeviceId(tempFileBox.getDeviceId()).setCabinetNum(tempFileBox.getCabinetNum()).setNumber(tempFileBox.getNumber()).setCode(BindApplication.BIND_CODE).setStatus(BindApplication.PENDING_STATUS).setApplicationTime(timeStamp).save();
		if (bindApplicationDao.findFirst("select * from bind_application where owner_id = ? and box_id = ? and code = ? and status = ?", ownerId, boxId, BindApplication.BIND_CODE, BindApplication.PENDING_STATUS) != null) {
			return RetKit.ok("发出申请成功");
		} else {
			return RetKit.fail("发出申请失败");
		}
	}
	
	/**
	 * 用户发出解绑申请，信息会存储到bind_application表中，然后等待管理员操作
	 * @param boxId
	 * @param accessToken
	 * @return
	 */
	public RetKit unBindBox(Integer boxId, String accessToken) {
		Integer id = boxId;
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		FileBox tempFileBox = fileBoxDao.findById(boxId);
		String deviceId = tempFileBox.getDeviceId();
		Integer cabinetNum = tempFileBox.getCabinetNum();
		Integer number = tempFileBox.getNumber();
		Integer ownerId = loginUser.getInt("id");
		if (tempFileBox.getBoxStatus() == FileBox.BOX_STATUS_USED) {
			return new RetKit().setFail("该信报箱有存放，请取走后再解除绑定");
		}
		if (ownerDeviceDao.findFirst("select * from owner_device where owner_id = ? and device_id = ? and cabinet_num = ? and number = ?", ownerId, deviceId ,cabinetNum, number) == null) {
			return RetKit.fail("你没有绑定该信报箱，不能申请解绑");
		}
		if (bindApplicationDao.findFirst("select * from bind_application where owner_id = ? and box_id = ? and code = ?", ownerId, id, BindApplication.UNBIND_CODE) != null) {
			return RetKit.fail("已发出过申请，请耐心等待管理员操作");
		}
		Date timeStamp = BindApplication.getCurrentTimeStamp();
		String phone = fileBoxOwnerDao.findById(ownerId).getPhone();
		if (new BindApplication().setOwnerId(ownerId).setPhone(phone).setBoxId(boxId).setDeviceId(deviceId).setCabinetNum(tempFileBox.getCabinetNum()).setNumber(tempFileBox.getNumber()).setCode(BindApplication.UNBIND_CODE).setStatus(BindApplication.PENDING_STATUS).setApplicationTime(timeStamp).save()) {
			return RetKit.ok("成功发出申请解绑");
		} else {
			return RetKit.fail("发出申请失败");
		}
	}
	
	public RetKit repairApplication(String accessToken, Integer boxId, UploadFile picture, Integer type, String detail) {
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer ownerId = loginUser.getInt("id");
		FileBox tempFileBox = fileBoxDao.findById(boxId);
		String deviceId = tempFileBox.getDeviceId();
		Integer cabinetNum = tempFileBox.getCabinetNum();
		Integer number = tempFileBox.getNumber();
		if (ownerDeviceDao.findFirst("select * from owner_device where owner_id = ? and device_id = ? and cabinet_num = ? and number = ?", ownerId, deviceId ,cabinetNum, number) == null) {
			return RetKit.fail("你没有绑定该信报箱");
		}
		if (repairApplicatonDao.findFirst("select * from repair_application where owner_id = ? and type = ? and device_id = ? and cabinet_num = ? and number = ?", ownerId, type, deviceId, cabinetNum, number) != null) {
			return RetKit.fail("该箱已报修");
		}
		RetKit retKit = RepairApplication.savePhoto(picture);
		String photoUrl = retKit.getStr("photoUrl");
		Date timeStamp = BindApplication.getCurrentTimeStamp();
		String phone = fileBoxOwnerDao.findById(ownerId).getPhone();
		if (new RepairApplication().setType(type).setOwnerId(ownerId).setPhone(phone).setDeviceId(deviceId).setCabinetNum(cabinetNum).setNumber(number).setRepairDetail(detail).setPhotoUrl(photoUrl).setApplicationTime(timeStamp).setStatus(RepairApplication.PENDING_STATUS).save()) {
			return RetKit.ok("报修成功");
		} else {
			return RetKit.fail("报修失败");
		}
	}
	
	public RetKit bonusPoints(String accessToken, int pointOption) {
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer ownerId = loginUser.getInt("id");
		Date timeStamp = BindApplication.getCurrentTimeStamp();
		int newPoint = fileBoxOwnerDao.findById(ownerId).getPoints() + pointOption;
		if (newPoint < 0) {
			return RetKit.fail("积分不够操作失败");
		}
		switch (pointOption) {
		case BonusPoints.OPEN_DOOR_POINT:
			new BonusPoints().setOwnerId(ownerId).setPoints(BonusPoints.OPEN_DOOR_POINT).setDetail("开门加一分").setCreateTime(timeStamp).save();
			break;
		case BonusPoints.REPAIR_POINT:
			new BonusPoints().setOwnerId(ownerId).setPoints(BonusPoints.REPAIR_POINT).setDetail("维修加两份").setCreateTime(timeStamp).save();
			break;
		case BonusPoints.OPEN_DOOR_REDUCE:
			new BonusPoints().setOwnerId(ownerId).setPoints(BonusPoints.OPEN_DOOR_REDUCE).setDetail("错误开门减一分").setCreateTime(timeStamp).save();
			break;
		case BonusPoints.DAMAGE_REDUCE:
			new BonusPoints().setOwnerId(ownerId).setPoints(BonusPoints.DAMAGE_REDUCE).setDetail("人为破坏减三分").setCreateTime(timeStamp).save();
			break;
		default:
			break;
		}
		fileBoxOwnerDao.findById(ownerId).setPoints(newPoint).update();
		return RetKit.ok();
	}
	
	public RetKit bonusPointsRecord(String accessToken) {
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer owenrId = loginUser.getInt("id");
		List<BonusPoints> pointsRecord = bonusPointsDao.find("select * from bonus_points where owner_id = ?", owenrId);
		if (pointsRecord.size() > 0) {
			return RetKit.ok("data", pointsRecord);
		} else {
			return RetKit.fail("无数据");
		}
	}
	
}
