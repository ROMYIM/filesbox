package com.filebox.api.login;

import java.util.HashMap;
import java.util.Map;

import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.filebox.common.model.ApiSession;
import com.filebox.common.model.FileBoxOwner;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;


/**
 * @Description:TODO(api登录)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月11日
 */
public class ApiLoginService {
	public static final ApiLoginService me = new ApiLoginService();
	private final Account accountDao = new Account().dao();
	private final FileBoxOwner fileBoxOwnerDao = new FileBoxOwner().dao();
	
	
	public RetKit doLogin(JMap map){
		if(map.getInt("type") == FileConstant.access_admin){
			return adminLogin(map);
		}else if(map.getInt("type") == FileConstant.access_owner){
			return userLogin(map);
		}else{
			return RetKit.fail("暂不支持其他登录角色");
		}
	}
	
	private RetKit adminLogin(JMap map) {
		Account loginAccount = accountDao.findFirst("select * from account where phone =? limit 1",map.getStr("phone").trim());
		RetKit retKit = new RetKit();
		if(loginAccount == null){
			return retKit.setFail("用户不存在");
		}
		if(loginAccount.isStatusLock()){
			return retKit.setFail("用户被锁定");
		}
//		String hashedPass = HashKit.md5(map.getStr("password").trim());
		//未通过密码验证
		if(loginAccount.getPassword().equals(map.getStr("password").trim()) == false){
			return retKit.setFail("用户名或密码不正确");
		}
		String accessToken = StrKit.getRandomUUID();
		//创建session
		if(!createSession(loginAccount.getId(), accessToken, FileConstant.access_admin)){
			return retKit.setFail("保存token到数据库失败");
		}
		
		Map<String,Object> accessTokenMap = new HashMap<String, Object>();
		accessTokenMap.put(FileConstant.accessToken, accessToken);
		retKit.set("data",accessTokenMap);
		retKit.setOk("登录成功");
		
		Record record = loginAccount.toRecord();
		record.set(FileConstant.accessToken, accessToken);
		CacheKit.put(FileConstant.apiAccountCache, accessToken, record);
		//保存loginAccount到缓存
		return retKit;
		
		
	}
	
	/**
	 * 归属人登录
	 */
	public RetKit userLogin(JMap map) {
		RetKit retKit = new RetKit();
		FileBoxOwner fileBoxOwner = fileBoxOwnerDao.findFirst(
				"select * from file_box_owner where phone = ? and password = ? limit 1", map.getStr("phone"),
				map.getStr("password"));

		if (fileBoxOwner == null) {
			return retKit.setFail("手机号或密码不正确");
		}
		String accessToken = StrKit.getRandomUUID();

		// 创建session
		if (!createSession(fileBoxOwner.getId(), accessToken, FileConstant.access_courier)) {
			return retKit.setFail("保存token到数据库失败");
		}
		
		Map<String, Object> accessTokenMap = new HashMap<String, Object>();
		accessTokenMap.put(FileConstant.accessToken, accessToken);
		accessTokenMap.put(FileConstant.ownerId, fileBoxOwner.getId());
		retKit.set("data",accessTokenMap);
		retKit.setOk("登录成功");
		
		return retKit;

	}
	
	private boolean createSession(int accountId,String sessionId,int access){
		Record record = Db.findFirst("select * from api_session where account_id = ? and access = ?",accountId,access);
		long liveSeconds = 30 * 60;
		// expireAt 用于设置 session 的过期时间点，需要转换成毫秒
		long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);
		if (record!=null) {
			int count = Db.update("update api_session set token = ? , expireAt = ? where account_id = ? ",sessionId,expireAt,accountId);
			return count > 0;
		}else{
			ApiSession session = new ApiSession();
			session.setAccountId(accountId);
			session.setToken(sessionId);
			session.setAccess(access);
			session.setExpireAt(expireAt);
			return session.save();
		}
		
	}
	
	public RetKit logout(String accessToken){
		CacheKit.remove(FileConstant.apiAccountCache,accessToken);
		return RetKit.ok();
	}
	
	public Record getLoginAccount(String accessToken){
		return CacheKit.get(FileConstant.apiAccountCache, accessToken);
	}
}
