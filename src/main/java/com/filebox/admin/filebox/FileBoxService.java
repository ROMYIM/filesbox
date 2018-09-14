package com.filebox.admin.filebox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.WebSocketContainer;

import com.filebox.admin.device.FileDeviceService;
import com.filebox.api.admin.Device;
import com.filebox.api.login.ApiLoginService;
import com.filebox.api.websocket.WebSocketController;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.BindApplication;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.OwnerDevice;
import com.jfinal.json.Json;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.Str;

/**
 * @Description:TODO(箱子业务逻辑层)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月25日
 */
public class FileBoxService {
	public static final FileBoxService me = new FileBoxService();
	private static final FileBox fileBoxDao = new FileBox().dao();
	static final OwnerDevice ownerDeviceDao = new OwnerDevice().dao();

	/**
	 * 箱子列表
	 */
	public List<FileBox> boxList(String deviceId) {
		return fileBoxDao.find("select * from file_box where device_id = ? order by cabinet_num,number", deviceId);
	}
	
	/**
	 * 箱子列表（包含绑定人信息）
	 */
	public List<Record> boxListWithOwnerInfo(String deviceId) {
		return Db.find("select  c.*,d.phone, e.unit as 'owner_unit' ,e.floor as 'owner_floor',e.room_address as 'owner_room_address'  from (select a.*,b.owner_id from file_box a left join owner_device b on "
				+ "a.device_id = b.device_id and a.cabinet_num = b.cabinet_num and a.number = b.number "
				+ "order by a.cabinet_num,number) as c left join file_box_owner d on c.owner_id = d.id "
				+ "left join owner_detail e on c.owner_id = e.owner_id and e.device_id = c.device_id where c.device_id = ? "
				+ "order by c.cabinet_num,number", deviceId);
	}
	
	/**
	 * 箱子列表（未使用和指定owner_id的）
	 */
	public List<Record> boxListNotUseInfoOrIncludeSepcialOwner(String deviceId,int ownerId) {
		return Db.find("select  c.*,d.phone, e.unit as 'owner_unit' ,e.floor as 'owner_floor',e.room_address as 'owner_room_address'  from (select a.*,b.owner_id from file_box a left join owner_device b on "
				+ "a.device_id = b.device_id and a.cabinet_num = b.cabinet_num and a.number = b.number "
				+ "order by a.cabinet_num,number) as c left join file_box_owner d on c.owner_id = d.id "
				+ "left join owner_detail e on c.owner_id = e.owner_id and e.device_id = c.device_id where c.device_id = ? "
				+ "and (c.owner_id = ? or isnull(c.owner_id))"
				+ "order by c.cabinet_num,number", deviceId,ownerId);
	}

	/**
	 * 获取信报箱信息
	 * 
	 */
	public FileBox getFileBox(int id) {
		return fileBoxDao.findFirst("select * from file_box where id = ?", id);
	}

	/**
	 * 获取信报箱信息
	 */
	public FileBox getFileBox(String deviceId, int cabinetNum, int boxNum) {
		return fileBoxDao.findFirst("select * from file_box " + "where device_id = ? and  cabinet_num=? and number = ?",
				deviceId, cabinetNum, boxNum);
	}

	/**
	 * 更改信报箱-使用状态
	 */
	public RetKit updateDeviceStatus(String deviceId, int status) {
		if (status == FileDevice.STATUS_DISABLE) {
			return FileDeviceService.me.stopDevice(deviceId);
		}
		if (status == FileDevice.STATUS_OK) {
			return FileDeviceService.me.startDevice(deviceId);
		}
		return RetKit.fail();
	}

	/**
	 * 更新箱门状态
	 */
	public RetKit updateBoxsStatus(String deviceId, Device cabinet) {
		if (cabinet.getDoorStatus() == null) {
			return RetKit.fail("箱子状态不能为空");
		}
		Record record = Db.findFirst(
				"select count(*) as count from file_box " + "where device_id = ? and cabinet_num = ? ", deviceId,
				cabinet.getCabinet_num());
		int Boxs = Integer.valueOf(record.getLong("count").toString());
		int doorStatus[] = cabinet.getDoorStatus();
		int count = 0;
		for (int i = 0; i <= Boxs; i++) {
			int tempCount = Db.update(
					"update file_box set door_status = ? " + "where device_id = ? and cabinet_num = ? and number = ?",
					doorStatus[i], deviceId, cabinet.getCabinet_num(), i + 1);
			count = count + tempCount;
		}
		System.out.println("更新成功个数：" + count + "个");
		return RetKit.ok("更新成功个数：" + count + "个");

	}

	/**
	 * 箱子报修
	 */
	public RetKit repair(FileBox box){
		FileBox model = fileBoxDao.findFirst("select * from file_box "
				+ "where device_id = ? and number = ? and cabinet_num = ?", box.getDeviceId(),box.getNumber(),box.getCabinetNum());
		if(model == null){
			return RetKit.fail("箱子不存在");
		}
		if(box.isCodeNormal()){ // 如果箱子正常使用
			if(FileDeviceService.me.isDeviceStop(box.getDeviceId())){  //如果箱子要启用 要看主控板的是否停用
				box.setBoxStatus(FileBox.BOX_STATUS_STOP);
			}else{
				box.setBoxStatus(FileBox.BOX_STATUS_WAIT);
			}
		}else{
			box.setBoxStatus(FileBox.BOX_STATUS_REPAIR);
		}
		box.setId(model.getId());
		return box.update()?RetKit.ok():RetKit.fail();
	}
	
	/**
	 * 箱子报修界面执行更新
	 */
	public RetKit repairUpdate(String deviceId,FileBox model){
		if(model==null|| model.getId() == null){
			return RetKit.fail("msg","提交信息不能为空");
		}
		
		FileBox tempBox = getFileBox(model.getId());
		
		if(tempBox.getBoxStatus().equals(FileBox.BOX_STATUS_USED)){
			return RetKit.fail("msg","该箱子被使用中，暂时无法报修");
		}
		
		if(model.getCode() == FileBox.CODE_0){
			//如果设备被停用 则几时启用箱子都变成停用
			if(FileDeviceService.me.isDeviceStop(deviceId)){
				model.setBoxStatus(FileBox.BOX_STATUS_STOP);
			}else{
				model.setBoxStatus(FileBox.BOX_STATUS_WAIT);
			}
		}else{
			model.setCode(model.getCode());
		}
		if (model.update()) {
			return RetKit.ok("msg", "保存成功");
		}
		return RetKit.fail("msg", "保存失败");
	}
	
	public RetKit openFileBox(int boxId){
		FileBox box = fileBoxDao.findById(boxId);
		if(box == null){
			return RetKit.fail("箱子ID有误");
		}
		if(box.getBoxStatus().equals(FileBox.BOX_STATUS_USED)){
			return RetKit.fail("该箱子被使用中，暂时无法打开");
		}
		java.util.Map<String,Object> map = new HashMap<String ,Object>();
		map.put("cabinet_num", box.getCabinetNum());
		map.put("box_number", box.getNumber());
		map.put("type", FileBox.SOCKET_TYPE_OPEN);
		RetKit retKit = WebSocketController.sendMsgToUser(box.getDeviceId(), JsonKit.toJson(map));
		if(!retKit.success()){
			return retKit;
		}
		return RetKit.ok("发送成功");
	}
	
	/**
	 * 开放给手机端开锁使用
	 */
	public RetKit openFileBox(String deviceId,int cabinetNum,int number) {
		if(number == 88){
			FileBox box = fileBoxDao.findFirst("select * from file_box"
					+ " where device_id =? and number = ? and cabinet_num = ?",deviceId,2,cabinetNum);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("box_number", number);
			map.put("cabinet_num", cabinetNum);
			map.put("type", FileBox.SOCKET_TYPE_OPEN);
			RetKit ret = WebSocketController.sendMsgToUser(box.getDeviceId(), JsonKit.toJson(map));
			if (!ret.success()) {
				return ret;
			}
			return RetKit.ok("发送成功");
		}else{
			FileBox box = fileBoxDao.findFirst("select * from file_box"
					+ " where device_id =? and number = ? and cabinet_num = ?",deviceId,number,cabinetNum);
			if (box==null) {
				return RetKit.fail("箱子不存在");
			}
			if (box.getBoxStatus().equals(FileBox.BOX_STATUS_USED)) {
				return RetKit.fail("该箱子被使用中，暂时无法打开");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("box_number", number);
			map.put("cabinet_num", cabinetNum);
			map.put("type", FileBox.SOCKET_TYPE_OPEN);
			RetKit ret = WebSocketController.sendMsgToUser(box.getDeviceId(), JsonKit.toJson(map));
//			RetKit ret = RetKit.ok();
			if (!ret.success()) {
				return ret;
			}
			return RetKit.ok("发送成功");
		}
	}
	
	public RetKit openFileBox(String accessToken, String deviceId, int cabinetNum, int number) {
		Record loginUser = ApiLoginService.me.getLoginAccount(accessToken);
		Integer owenrId = loginUser.getInt("id");
		if (ownerDeviceDao.findFirst("select * from owner_device where owner_id = ? and device_id = ? and cabinet_num = ? and number = ?", owenrId, deviceId, cabinetNum, number) != null) {
			return openFileBox(deviceId, cabinetNum, number);
		}
		return RetKit.fail("没有绑定信报箱,不能开锁");
	}

	public int getBindApplicationCount(String deviceId) {
		int count = Db.find("select * from bind_application where device_id = ? and code = ?", deviceId, BindApplication.BIND_CODE).size();
		return count;
	}
	
	public int getUnbindApplicationCount(String deviceId) {
		return Db.find("select * from bind_application where device_id = ? and code = ?", deviceId, BindApplication.UNBIND_CODE).size();
	}
}
