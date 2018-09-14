package com.filebox.admin.filebox.sepcialuser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DataBindingException;

import com.filebox.admin.filebox.owner.AddOrModifyBoxOwnerValidator;
import com.filebox.api.websocket.WebSocketController;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.SpecialUser;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

/**
* @Description:TODO(特殊用户管理)
* @author 作者 : jinghui.su
* @date 创建时间：2017年7月4日
*/
public class SpecialUserService {
	public static final SpecialUserService me = new SpecialUserService();
	public static final SpecialUser specialUserDao = new SpecialUser();

	/**
	 * 特殊用户信息
	 */
	public SpecialUser SpecialUserInfo(int id){
		return specialUserDao.findFirst("select * from special_user where id = ?",id);
	}
	
	/**
	 * 特殊用户信息
	 */
	public SpecialUser SpecialUserInfo(String phone,String deviceId){
		return specialUserDao.findFirst("select * from special_user where phone = ? and device_id = ?",phone,deviceId);
	}
	
	/**
	 * 特殊用户列表
	 */
	public List<SpecialUser> specialUserList(String deviceId){
		return specialUserDao.find("select * from special_user where device_id = ? ",deviceId);
	}
	
	
	/**
	 * 未同步到终端用户列表
	 */
	public List<SpecialUser> noTerminalSpecialUserList(String deviceId){
		return specialUserDao.find("select * from special_user where device_id = ? and terminal = ? ",deviceId,SpecialUser.TERMINAL_NO);
	}
	
	/**
	 * 可用的用户列表
	 */
	public List<SpecialUser> normalSpecialUserList(String deviceId){
		return specialUserDao.find("select * from special_user where device_id = ? and status = ?",deviceId,SpecialUser.STATUS_OK);
	} 
	
	/**
	 * 新增特殊用户
	 */
	public RetKit addOrModifySpecialUser(String phone,String deviceId,String icCardId,int status){
		SpecialUser specialUser = specialUserDao.findFirst("select * from special_user where phone = ? and device_id = ?",phone,deviceId);
		boolean succ = false;
		if(specialUser == null){
			SpecialUser  newSpecialUser = new SpecialUser();;
			newSpecialUser.setPhone(phone);
			newSpecialUser.setDeviceId(deviceId);
			newSpecialUser.setIcCard(icCardId);
			newSpecialUser.setStatus(SpecialUser.STATUS_OK);
			newSpecialUser.setTerminal(SpecialUser.TERMINAL_NO);  //将设为未同步到终端
			newSpecialUser.setSysupdate(new Date());
			succ = newSpecialUser.save();
		}else{
			specialUser.setPhone(phone);
			specialUser.setDeviceId(deviceId);
			specialUser.setIcCard(icCardId);
			specialUser.setStatus(status);
			specialUser.setTerminal(SpecialUser.TERMINAL_NO);  //将设为未同步到终端
			specialUser.setSysupdate(new Date());
			succ = specialUser.update();
		}
		
		return succ ? RetKit.ok() : RetKit.fail("修改特殊用户信息失败，请联系系统管理员");
	}
	
	
	/**
	 * 更改特殊用户状态
	 */
	public RetKit updateSpecialUserStatus(int specialUserId, int status){
		SpecialUser specialUser = specialUserDao.findFirst("select * from special_user where id = ?",specialUserId);
		specialUser.setStatus(status);
		specialUser.setTerminal(SpecialUser.TERMINAL_NO);  //将设为未同步到终端
		boolean succ = specialUser.update();
		
		return succ ? RetKit.ok():RetKit.fail("更改失败");
		
	}
	
	/**
	 * 删除特殊用户
	 */
	public RetKit deleteSpecialUser(int specialUserId,String deviceId){
		List<SpecialUser> specialUsers = specialUserList(deviceId); 
		if(specialUsers!= null && specialUsers.size() > 0){   //删除用户的时候随便将这个设备的一个特殊用户设置成未同步到终端，目的是让用户知道删除了还没有同步到终端
			SpecialUser specialUser = specialUsers.get(0);
			specialUser.setTerminal(0);
			specialUser.update();
		}
		
		int n = Db.update("delete from special_user where id = ?", specialUserId);
		if (n > 0) {
			return RetKit.ok("msg", "删除成功");
		} else {
			return RetKit.fail("msg", "删除失败");
		}
	}
	
	
	/**
	 * 更新终端特殊用户到终端
	 */ 
	public RetKit sendSpecialToTerminal(String deviceId){
		List<SpecialUser> specialUsers = normalSpecialUserList(deviceId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("specialUserList", specialUsers);
		map.put("type", FileBox.SOCKET_TYPE_UPDATE_SEPCIAL_USER);
		RetKit ret = WebSocketController.sendMsgToUser(deviceId, JsonKit.toJson(map));
		if (!ret.success()) {
			return ret;
		}
		
		for(SpecialUser item:specialUsers){  //发送成功设置每个特殊用户为已 
			item.setTerminal(SpecialUser.TERMINAL_OK);
			item.update();
		}
		
		return RetKit.ok("发送成功");
	}
	
}
