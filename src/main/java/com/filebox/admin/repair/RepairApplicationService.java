package com.filebox.admin.repair;

import java.util.Date;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.BindApplication;
import com.filebox.common.model.RepairApplication;
import com.filebox.common.model.RepairMan;
import com.filebox.utils.CytSdkUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class RepairApplicationService {

	public static final RepairApplicationService me = new RepairApplicationService();
	static final RepairApplication repairApplicationDao = new RepairApplication().dao();
	static final RepairMan repairManDao = new RepairMan().dao();
	
	public RepairApplicationService() {
		// TODO Auto-generated constructor stub
	}

	public Page<Record> repairApplicationListByPage(int pn, int ps, String deviceId) {
		return Db.paginate(pn, ps, "select * ", "from repair_application where device_id = ?", deviceId);
	}
	
	public Page<Record> repairManListByPage(int pn, int ps) {
		return Db.paginate(pn, ps, "select * ", "from repair_man");
	}
	
	public RepairApplication getRepairInfomation(int id) {
		return repairApplicationDao.findById(id);
	}
	
	public RetKit sendMessage(String phone, RepairApplication model) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("设备号：" + model.getDeviceId() + " ");
		sBuilder.append("信报箱cab：" + model.getCabinetNum() + " ");
		sBuilder.append("信报箱号：" + model.getNumber() + " ");
		sBuilder.append("报修时间：" + model.getApplicationTime());
		return CytSdkUtil.sendMessage(phone, sBuilder.toString());
	}
	
	public RetKit acceptRepair(Integer id, Integer applicationId) {
		Date timeStamp = BindApplication.getCurrentTimeStamp();
		if (repairApplicationDao.findById(applicationId).setOrderTime(timeStamp).setStatus(RepairApplication.REAPIRING_STATUS).setRepairManId(id).setRepairManPhone(repairManDao.findById(id).getPhone()).update()) {
			return RetKit.ok("接单成功");
		} else {
			return RetKit.fail("接单失败");
		}
	}
	
}
