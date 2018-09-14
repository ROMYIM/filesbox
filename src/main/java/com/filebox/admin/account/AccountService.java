package com.filebox.admin.account;

import java.util.ArrayList;
import java.util.List;

import com.filebox.admin.login.LoginService;
import com.filebox.common.kit.RandomKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.filebox.common.model.AccountDevice;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.Session;
import com.filebox.common.model.SysRole;
import com.filebox.utils.CytSdkUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.JMap;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * @Description:TODO()
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月9日
 */
public class AccountService {
	public static final AccountService me = new AccountService();
	public static final String managerBoxCache = "managerBox";
	public static final String verifyCodeCach = "verifyCode";
	public static AccountDevice accountDeviceDao = new AccountDevice();
	public static final Account accountDao = new Account();
	public static final FileDevice fileDeviceDao = new FileDevice();
	public static final SysRole sysRoleDao = new SysRole();

	public RetKit list() {
		return RetKit.ok("list", accountDao.find("select * from account"));
	}
	
	public RetKit listByPage(int ps, int pn, JMap map){
		JMap sqlMap = createSearchSql(map);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		return RetKit.ok("page", Db.paginate(pn, ps, "select * ", sql, para));
	}
	

	// 获取管理员管理的信报箱
	public static List<AccountDevice> getManagerBox(int accountId) {
		return accountDeviceDao.findByCache(managerBoxCache, accountId,
				"select * from account_device where account_id = ? ", accountId);
	}

	/**
	 * 重置密码
	 */
	public Ret updatePassword(Account loginAccount, String verifyCode) {
		String verCode = CacheKit.get(verifyCodeCach, loginAccount.getPhone());
		if (verCode == null || !verifyCode.equals(verCode)) {
			return Ret.fail("msg", "验证码不正确");
		}
		String password = loginAccount.getPassword();
		if (password.length() < 6) {
			return Ret.fail("msg", "新密码长度不能少于6");
		}
		loginAccount.setPassword(HashKit.md5(password));
		boolean succ = loginAccount.update();
		if (succ) {
			// 删除验证码缓存
			CacheKit.remove(verifyCodeCach, loginAccount.getPhone());
			// 更改密码需要重新登录
			List<Session> sessionList = Session.dao.find("select * from session where accountId = ? ",
					loginAccount.getId());
			if (sessionList != null) {
				for (Session session : sessionList) { // 处理多客户端同时登录的多session记录
					LoginService.me.logout(session.getId()); // 清除登录cache，强制退出
				}
			}
			return Ret.ok("msg", "密码更新成功");
		} else {
			return Ret.fail("msg", "未找到账号，请联系管理员");
		}
	}

	public RetKit sendVerifyCode(Account loginAccount, String sessionId) {
		String verifCodeCache = CacheKit.get(verifyCodeCach, loginAccount.getPhone());
		if (verifCodeCache != null) {
			return RetKit.fail("请不要重复发送验证码");
		}
		String verifyCode = RandomKit.getRandomPsw(4); // 随机生成4位数
		// RetKit retKit = CytSdkUtil.sendMessage(loginAccount.getPhone(),
		// "验证码："+verifyCode+ "，您正在修改密码，如非本人操作，请不要理会");

		RetKit retKit = RetKit.ok("发送成功");
		if (retKit.success()) {
			CacheKit.put(verifyCodeCach, loginAccount.getPhone(), verifyCode);
		}
		return retKit;
	}

	/**
	 * 禁用管理员
	 */
	public RetKit disable(int id) {
		if (id == 1) {
			return RetKit.fail("msg", "不允许禁用初始系统管理员");
		}
		int n = Db.update("update account set status = ? where id = ? ", Account.STATUS_DISABLE, id);
		// 锁定后，强制退出登录，避免继续搞破坏
		List<Session> sessionList = Session.dao.find("select * from session where accountId = ? ", id);
		if (sessionList != null) {
			for (Session session : sessionList) { // 处理多客户端同时登录后的多session记录
				LoginService.me.logout(session.getId()); // 清除登录cache，强制退出
			}
		}
		if (n > 0) {
			return RetKit.ok("msg", "禁用成功");
		} else {
			return RetKit.fail("msg", "禁用失败");
		}
	}

	/**
	 * 启用
	 */
	public RetKit undisable(int id) {
		int n = Db.update("update account set status = ? where id = ?", Account.STATUS_OK, id);
		if (n > 0) {
			return RetKit.ok("msg", "启用成功");
		} else {
			return RetKit.fail("msg", "启用失败");
		}
	}

	/**
	 * 删除管理员
	 */
	public RetKit delete(int id) {
		if (id == 1) {
			return RetKit.fail("msg", "不允许删除初始系统管理员");
		}
		int n = Db.update("delete from account where id = ?", id);
		if (n > 0) {
			return RetKit.ok("msg", "删除成功");
		} else {
			return RetKit.fail("msg", "删除失败");
		}
	}

	/**
	 * 获取个人信息
	 */
	public Account getAccount(String id) {
		return accountDao.findFirst("select * from account where id = ?", id);
	}

	/**
	 * 获取管理员管理的信报箱
	 */
	public static String getManagerBoxStr(int accountId) {
		String str = "";
		List<AccountDevice> list = accountDeviceDao.findByCache(managerBoxCache, accountId,
				"select * from account_device where account_id = ? ", accountId);
		;
		boolean first = true;
		for (AccountDevice model : list) {
			if (first) {
				first = false;
				str = str + model.getDeviceId();
			} else {
				str = str + "," + model.getDeviceId();
			}
		}
		return str;
	}

	// 获取所有信报箱
	public static List<FileDevice> getCablinets() {
		return fileDeviceDao.find("select * from file_device");
	}

	// 查询信报箱
	public RetKit searchCablinets(String searchStr) {
		return RetKit.ok("list", fileDeviceDao.find(
				"select * from file_device where id like '%" + searchStr + "%' or name like '%" + searchStr + "%'"));
	}

	// 获取系统角色
	public static List<SysRole> getSysRoles() {
		return sysRoleDao.find("select * from sys_role where status = ?", SysRole.STATUS_OK);
	}

	private boolean isExistPhone(String phone) {
		return accountDao.findFirst("select * from account where phone = ? ", phone) != null;
	}
	
	
	/**
	 * 更新管理员和管理的信报箱
	 */
	@Before(Tx.class)
	public RetKit update(Account account,String devices){
		Account model = accountDao.findFirst("select * from account where phone = ? ",account.getPhone());
		account.setId(model.getId());
		Db.update("delete from account_device where account_id = ?",model.getId());//删除之前管理的快递柜
		if (account.isNormalAdmin()) {
			List<AccountDevice> modelList  = new ArrayList<AccountDevice>();
			String DevicesArr[] = devices.split(",");
			for (String str : DevicesArr) {
				try {
					AccountDevice accountDevice = new AccountDevice();
					accountDevice.setAccountId(model.getId());
					accountDevice.setDeviceId(str);
					modelList.add(accountDevice);
				} catch (Exception e) {
					return RetKit.fail("msg", "数据不符合格式");
				}
			}
			account.update();//更新信息
			Db.batchSave(modelList, modelList.size());
		}else{
			account.update();//更新信息
		}
		return RetKit.ok();
	}
	

	/**
	 * 新增管理员和管理的信报箱
	 */
	public RetKit save(Account account, String devices) {
		if (isExistPhone(account.getPhone())) {
			return RetKit.fail("该手机号已存在");
		}
		// 生成6位数随机密码并且加密
		String password = RandomKit.getRandomPsw(6);
		// account.setPassword(HashKit.md5("123456"));
		account.setPassword(HashKit.md5(password));
		boolean succ = account.save();
		if (account.isNormalAdmin()) {
			List<AccountDevice> modelList = new ArrayList<AccountDevice>();
			AccountDevice accountDevice = new AccountDevice();
			String DevicesArr[] = devices.split(",");
			for (String str : DevicesArr) {
				try {
					accountDevice.setAccountId(account.getId());
					accountDevice.setDeviceId(str);
					modelList.add(accountDevice);
				} catch (Exception e) {
					return RetKit.fail("数据不符合格式");
				}
			}
			Db.batchSave(modelList, modelList.size());
		}
		if (succ) {
			CytSdkUtil.sendMessage(account.getPhone(), "您已被添加为管理员，初始密码为：" + password + "，请妥善保存");
		}
		return RetKit.ok("保存成功");
	}
	
	private JMap createSearchSql(JMap map) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		sb.append("from account ");
		sb.append("where 1=1 ");
		if (StrKit.notBlank(map.getStr("name"))) {
			sb.append("and name like '%" + map.getStr("name") + "%' ");
		}
		if (StrKit.notBlank(map.getStr("phone"))) {
			sb.append("and phone like '%" + map.getStr("phone") + "%' ");
		}

		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}

}
