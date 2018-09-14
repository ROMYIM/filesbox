package com.filebox.admin.device;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.Expand;

import com.filebox.admin.filebox.FileBoxService;
import com.filebox.admin.login.LoginService;
import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileDevice;
import com.jfinal.kit.JMap;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;


/**
 * @Description:TODO(设备服务)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月26日
 */
public class FileDeviceService {
	public static final FileDeviceService me = new FileDeviceService();
	public static final FileDevice fileDeviceDao = new FileDevice().dao();

	/**
	 * 停用信报箱
	 */
	public RetKit stopDevice(String deviceId) {
		int count = Db.update("update file_device set status = ? where id = ?", FileDevice.STATUS_DISABLE, deviceId);
		if (count > 0) {
			Db.update("update file_box set box_status = ? where device_id = ? and box_status = ?",
					FileBox.BOX_STATUS_STOP, deviceId, FileBox.BOX_STATUS_WAIT);
			return RetKit.ok("停用成功");
		}
		return RetKit.fail("停用失败");
	}

	/**
	 * 启用信报箱
	 */
	public RetKit startDevice(String deviceId) {
		int count = Db.update("update file_device set status = ? where id = ?", FileDevice.STATUS_OK, deviceId);
		if (count > 0) {
			// 更新停用箱子状态
			Db.update("update file_box set box_status = ? where device_id = ? and box_status != ?",
					FileBox.BOX_STATUS_WAIT, deviceId, FileBox.BOX_STATUS_REPAIR);
			return RetKit.ok("启用成功");
		}
		return RetKit.fail("启用失败");
	}

	/**
	 * 判断箱子是否停用
	 */
	public boolean isDeviceStop(String deviceId) {
		return getDevice(deviceId).isDisable();
	}

	public FileDevice getDevice(String deviceId) {
		return fileDeviceDao.findFirst("select * from file_device where id = ?", deviceId);
	}

	public void setFiledeviceId(String sessionId, String expdeviceId) {
		CacheKit.put(FileConstant.fileDeviceId, sessionId, expdeviceId);
	}

	public String getFiledeviceId(String sessionId) {
		return CacheKit.get(FileConstant.fileDeviceId, sessionId);
	}

	/**
	 * 获取信报箱列表
	 */
	public Ret list(String sessionId) {
		Account account = CacheKit.get(LoginService.loginAccountCacheName, sessionId);
		if (account == null) {
			return Ret.fail("msg", "登录异常");
		}
		if (account.isSuperAdmin()) {
			return Ret.ok("list", fileDeviceDao.find("select a.*,b.name as operator_name from file_device a"
					+ " left join operator b on a.operator_id =  b.id"));
		} else {
			return Ret.ok("list",
					fileDeviceDao.find(
							"select b.*,c.name as operator_name from account_device a "
									+ "left join file_device b on a.device_id = b.id "
									+ "left join operator c on b.operator_id = c.id " + "where a.account_id = ? ",
							account.getId()));
		}
	}

	/**
	 * 分页获取信报箱列表
	 */
	public Ret listByPage(String sessionId, int ps, int pn, JMap map) {
		Account account = CacheKit.get(LoginService.loginAccountCacheName, sessionId);
		if (account == null) {
			return Ret.fail("msg", "登录异常");
		}
		JMap sqlMap = createSearchSql(map, account);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		if (account.isSuperAdmin()) { // 超级管理员
			return Ret.ok("page", Db.paginate(pn, ps, "select * ", sql, para));
		}else{
			return Ret.ok("page", Db.paginate(pn, ps, "select b.*,c.name as operator_name  ", sql, para));
		}
	}

	public RetKit updateDevice(FileDevice fileDevice) {
		FileDevice device = new FileDevice().findById(fileDevice.getId());
		if (device.getOperatorId() == null && fileDevice.getOperatorId() == null) {
			return RetKit.fail("运营商ID不能为空");
		}
		if (fileDevice.getOperatorId() != null && device.getOperatorId() != null
				&& fileDevice.getOperatorId() != device.getOperatorId()) {
			return RetKit.fail("不允许更改运营商");
		}

		boolean succ = fileDevice.update();
		if (succ) {
			// 更新箱子缓存
			// CacheKit.remove(FileBox.apiDevice, exp.getId());
			if (fileDevice.getStatus().equals(FileDevice.STATUS_OK)) {
				return startDevice(fileDevice.getId());
			} else {
				return stopDevice(fileDevice.getId());
			}
		}
		return RetKit.fail();
	}

	public RetKit deleteDevice(String deviceId) {
		List<Record> list = Db.find("select * from file_box where box_status = ? and device_id = ?",
				FileBox.BOX_STATUS_USED, deviceId);
		if (list.size() > 0) {
			return RetKit.fail("信报箱中还有使用中的箱子");
		}
		int count = Db.update("update file_device set status = ? where id = ? ", FileDevice.STATUS_DEL, deviceId);
		if (count > 0) {
			Db.update("update file_box set box_status = ? where device_id = ? ", FileBox.BOX_STATUS_DELETE, deviceId);
		}
		return RetKit.ok();
	}

	private JMap createSearchSql(JMap map, Account account) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		if (account.isSuperAdmin()) { // 超级管理员
			if(StrKit.isBlank(map.getStr("deviceName")) && StrKit.isBlank(map.getStr("operatorName"))){
				sb.append("from (select a.*,b.name as operator_name ");
				sb.append("from file_device a left join operator b on a.operator_id = b.id) ");
				sb.append("c where 1 =1 ");
			}else if(StrKit.notBlank(map.getStr("deviceName")) && StrKit.isBlank(map.getStr("operatorName"))){
				sb.append("from (select a.*,b.name as operator_name ");
				sb.append("from file_device a left join operator b on a.operator_id = b.id) ");
				sb.append("c where 1 =1 ");
				if (StrKit.notBlank(map.getStr("deviceName"))) {
					sb.append("and ((c.name like '%"+map.getStr("deviceName")+"%' ");
					sb.append("or c.id like '%"+map.getStr("deviceName")+"%'))");
				}
			}else{
				sb.append("from (select a.*,b.name as operator_name ");
				sb.append("from file_device a left join operator b on a.operator_id = b.id) ");
				sb.append("c where 1 =1 ");
				if (StrKit.notBlank(map.getStr("deviceName"))) {
					sb.append("and ((c.name like '%"+map.getStr("deviceName")+"%' ");
					sb.append("or c.id like '%"+map.getStr("deviceName")+"%')");
				}
				if (StrKit.isBlank(map.getStr("deviceName"))){
					if (StrKit.notBlank(map.getStr("operatorName"))) {
						sb.append("and c.operator_name = ? ");
						paraList.add(map.getStr("operatorName"));
					}
				}else{
					if (StrKit.notBlank(map.getStr("operatorName"))) {
						sb.append("and c.operator_name = ? )");
						paraList.add(map.getStr("operatorName"));
					}
				}
				
			}
		} else {
			sb.append("from account_device a ");
			sb.append("left join file_device b on a.device_id = b.id ");
			sb.append("left join operator c on b.operator_id = c.id ");
			sb.append("where a.account_id = "+account.getId()+" ");
			if (StrKit.notBlank(map.getStr("deviceName"))) {
				sb.append("and (b.name like '%"+map.getStr("deviceName")+"%' ");
				sb.append("or b.id like '%"+map.getStr("deviceName")+"%')");
			}
			if (StrKit.notBlank(map.getStr("operatorName"))) {
				sb.append("and c.name = ? ");
				paraList.add(map.getStr("operatorName"));
			}
		}
		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}

}
