package com.filebox.admin.bind;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;import com.filebox.common.kit.RetKit;
import com.filebox.common.model.BindApplication;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.OwnerDetail;
import com.filebox.common.model.OwnerDevice;
import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class BindApplicationService {

	public static final BindApplicationService me = new BindApplicationService();
	static final BindApplication bindApplicationDao = new BindApplication().dao();
	static final OwnerDevice ownerDeviceDao = new OwnerDevice().dao();
	static final OwnerDetail ownerDetailDao = new OwnerDetail().dao();
	static final FileBox fileBoxDao = new FileBox().dao();
	
	public BindApplicationService() {
		// TODO Auto-generated constructor stub
	}

	public Page<Record> listByPage(int pn, int ps, String deviceId) {
		return Db.paginate(pn, ps, "select * ", "from bind_application where device_id = ?", deviceId);
	}
	public List<Record> getBindApplicationList(String deviceId) {
		return Db.find("select * from bind_application where device_id = ?", deviceId);
	}
	
	public BindApplication getAppicationInfo(int id) {
		return bindApplicationDao.findById(id);
	}
	
	public void updateApplication(Integer id, Date timeStamp) {
		bindApplicationDao.findById(id).setStatus(BindApplication.PASS_STATUS).setPassTime(timeStamp).update();
	}
	
	public void updateApplication(Integer ownerId, Integer boxId, Date timeStamp) {
		bindApplicationDao.findFirst("select * from bind_application where owner_id = ? and box_id = ?", ownerId, boxId).setStatus(BindApplication.PASS_STATUS).setPassTime(timeStamp).update();
	}
	
	public Record applicationDetail(Integer ownerId, Integer boxId) {
		Record isExist = Db.findFirst("select * from owner_detail where owner_id = ? limit 1", ownerId);
		if (isExist != null) {
			return Db.findFirst("select a.phone, b.owner_id, b.box_id, b.device_id, c.unit, c.floor, c.room_address, c.password_card"
					+ " from file_box_owner a, bind_application b, owner_detail c"
					+ " where a.id = b.owner_id and b.owner_id = c.owner_id and b.owner_id = ? and b.box_id = ?", ownerId, boxId);
		} else {
			return Db.findFirst("select owner_id, phone, device_id, box_id from bind_application where owner_id = ? and box_id = ?", ownerId, boxId);
		}
	}
	
	public RetKit bindBox(JMap map) {
		Integer ownerId = map.getInt("ownerId");
		Integer boxId = map.getInt("boxId");
		String deviceId = map.getStr("deviceId");
		String unit = map.getStr("unit");
		String floor = map.getStr("floor");
		String roomAddress = map.getStr("roomAddress");
		String passwordCard = map.getStr("passwordCard");
		FileBox tempFileBox = fileBoxDao.findById(boxId);
		Integer cabinetNum = tempFileBox.getCabinetNum();
		Integer number = tempFileBox.getNumber();
		OwnerDevice tempOwnerDevice = ownerDeviceDao.findFirst("select * from owner_device where owner_id = ? and device_id = ?", ownerId, deviceId);
		if (tempOwnerDevice == null) {
			new OwnerDetail().setOwnerId(ownerId).setDeviceId(deviceId).setPasswordCard(passwordCard).setUnit(unit).setFloor(floor).setRoomAddress(roomAddress).setOwnerDeviceStatus(1).save();
			new OwnerDevice().setOwnerId(ownerId).setDeviceId(deviceId).setCabinetNum(cabinetNum).setNumber(number).save();
		} else {
			tempOwnerDevice.setOwnerId(ownerId).setDeviceId(deviceId).setCabinetNum(cabinetNum).setNumber(number).update();
		}
		return RetKit.ok();
	}
	
	public RetKit unbindBox(Integer ownerId, Integer boxId) {
		FileBox tempFileBox = fileBoxDao.findById(boxId);
		String deviecId = tempFileBox.getDeviceId();
		Integer cabinetNum = tempFileBox.getCabinetNum();
		Integer number = tempFileBox.getNumber();
		OwnerDevice tempOwnerDevice = ownerDeviceDao.findFirst("select * from owner_device where owner_id = ? and device_id = ? and cabinet_num = ? and number = ?", ownerId, deviecId, cabinetNum, number);
		if (tempOwnerDevice != null) {
			tempOwnerDevice.delete();
			return RetKit.ok();
		} else {
			return RetKit.fail("没有该绑定信息");
		}
	}
}
