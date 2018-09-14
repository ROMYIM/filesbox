package com.filebox.admin.login;

import java.util.Date;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.filebox.common.model.Session;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * @Description:TODO(登录业务层)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月12日
 */
public class LoginService {
	public static final LoginService me = new LoginService();
	private final Account accountDao = new Account().dao();
	// 存放登录用户的cacheName
	public static final String loginAccountCacheName = "loginAccount";
	// 存放登录用户的phone
	public static final String loginPhone = "phone";

	// "jfinalId" 仅用于cookie名称，其他地方如cache中全部用的"sessionId"来做key
	public static final String sessionIdName = "jfinalId";

	/**
	 * 登陆成功返回sessionId 与 loginAccount,否则返回一个msg
	 */
	public RetKit login(String userName, String password, boolean remember, String loginIp) {
		userName = userName.toLowerCase().trim();
		password = password.trim();
		Account loginAccount = accountDao.findFirst("select *from account where phone = ? limit 1", userName);
		if (loginAccount == null) {
			return RetKit.fail("用户名或密码不正确");
		}
		if (loginAccount.isStatusLock()) {
			return RetKit.fail("账号已被锁定");
		}
		String hashedPassword = HashKit.md5(password);
		if (!loginAccount.getPassword().equals(hashedPassword)) {
			return RetKit.fail("用户名或密码不正确");
		}
		// 如果用户勾选保持登录，暂定过期时间为1年，否则为120分钟，单位为秒
		long liveSeconds = remember ? 1 * 365 * 24 * 60 * 60 : 120 * 60;
		// 传递给控制层的cookie
		int maxAgeInSeconds = (int) (remember ? liveSeconds : -1);
		// expireAt 用于设置session的过期时间点，需要转换成毫秒
		long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);
		// 保存登录session到数据库
		Session session = new Session();
		String sessionId = StrKit.getRandomUUID();
		session.setId(sessionId);
		session.setAccountId(loginAccount.getId());
		session.setExpireAt(expireAt);
		if (!session.save()) {
			return RetKit.fail("保存session 到数据库失败，请联系管理员");
		}
		loginAccount.removeSensitiveInfo(); // 移除password与salt属性值
		loginAccount.put("sessionId", sessionId);
		// 保存一份sessionId 到 loginAccount 备用
		CacheKit.put(loginAccountCacheName, session, loginAccount);
		// 保存登录
		createLoginLog(loginAccount.getId(), loginIp);
		//保存登录账号
		CacheKit.put(loginPhone, session, userName);

		return RetKit.ok(sessionIdName, sessionId).set(loginAccountCacheName, loginAccount).set("maxAgeInSeconds",
				maxAgeInSeconds); // 用于设置cookie的最大存活时间
	}

	/**
	 * 创建登录日志
	 */
	private void createLoginLog(Integer accountId, String loginIp) {
		Record loginLog = new Record().set("accountId", accountId).set("ip", loginIp).set("loginAt", new Date());
		Db.save("login_log", loginLog);
	}

	/**
	 * 退出登录
	 */
	public void logout(String sessionId) {
		if (sessionId != null) {
			CacheKit.remove(loginAccountCacheName, sessionId);
			Session.dao.deleteById(sessionId);
		}
	}

	/**
	 * 根据sessionId 获取登录用户信息
	 */
	public Account getLoginAccountWithSessionId(String sessionId) {
		return CacheKit.get(loginAccountCacheName, sessionId);
	}

	/**
	 * 通过 sessionId 获取登录用户信息 sessoin表结构：session(id, accountId, expireAt)
	 *
	 * 1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录，
	 * 如果没过期则先放缓存一份，然后再返回
	 */
	public Account loginWithSessionId(String sessionId, String loginIp) {
		Session session = Session.dao.findById(sessionId);
		if (session == null) { // session 不存在
			return null;
		}
		if (session.isExpired()) { // session 已过期
			session.delete(); // 被动式删除过期数据，此外还需要定时线程来主动清除过期数据
			return null;
		}

		Account loginAccount = accountDao.findById(session.getAccountId());
		// 找到loginAccount 并且是正常状态才允许登录
		if (loginAccount != null && loginAccount.isStatusOk()) {
			loginAccount.removeSensitiveInfo();
			loginAccount.put("sessionId", sessionId);
			CacheKit.put(loginAccountCacheName, sessionId, loginAccount);

			createLoginLog(loginAccount.getId(), loginIp);
			return loginAccount;
		}
		return null;

	}

}
