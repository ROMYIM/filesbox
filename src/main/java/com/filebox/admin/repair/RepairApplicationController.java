package com.filebox.admin.repair;

import com.filebox.common.constant.FileConstant;
import com.filebox.common.inteceptor.DeviceInterceptor;
import com.filebox.common.model.RepairApplication;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

@Before(DeviceInterceptor.class)
public class RepairApplicationController extends Controller {

	static final RepairApplicationService srv = RepairApplicationService.me;
	
	public RepairApplicationController() {
		// TODO Auto-generated constructor stub
	}

	public void index() {
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		String deviceId = getAttrForStr(FileConstant.deviceId);
		setAttr("page", srv.repairApplicationListByPage(pn, ps, deviceId));
		render("index.html");
	}
	
	public void repairMan() {
		int ps = getParaToInt("pageSize", 10);
		int pn = getParaToInt("pageNum", 1);
		int id = getParaToInt();
		setAttr("information", srv.getRepairInfomation(id));
		setAttr("page", srv.repairManListByPage(pn, ps));
		render("repair_man.html");
	}
	
	public void acceptRepair() {
		Integer repairManId = getParaToInt(0);
		Integer applicationId = getParaToInt(1);
		if (srv.acceptRepair(repairManId, applicationId).success()) {
			redirect("/repair");
		}
	}
	
	public void repairDetail() {
		int id = getParaToInt();
		setAttr("information", srv.getRepairInfomation(id).toRecord());
		render("repair_detail.html");
	}
}
