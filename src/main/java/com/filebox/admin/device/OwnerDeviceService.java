package com.filebox.admin.device;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBox;
import com.filebox.common.model.FileDevice;
import com.filebox.common.model.OwnerDetail;
import com.filebox.common.model.OwnerDevice;
import com.jfinal.plugin.activerecord.Db;

/**
* @Description:TODO(用户绑定的箱子)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月22日
*/
public class OwnerDeviceService {
	public static final OwnerDeviceService me = new OwnerDeviceService();
	public static final OwnerDevice ownerDeviceDao = new OwnerDevice().dao();
	public static final OwnerDetail ownerDetailDao = new OwnerDetail().dao();
	/**
	 * 删除归属人指定设备
	 */
	public RetKit deleteOwnerDevice(int ownerId,String deviceId) {
		int n = Db.update("delete from owner_device where owner_id = ? and device_id = ?", ownerId,deviceId);
		int d = Db.update("delete from owner_detail where owner_id = ? and device_id = ?",ownerId,deviceId);
		if (n > 0 && d > 0) {
			return RetKit.ok("msg", "删除成功");
		} else {
			return RetKit.fail("msg", "删除失败");
		}
	}
	/**
	 * 判断设备是否已经被绑定 如果已经被绑定返回绑定人的id
	 */
	public int getBoxBinderId(FileBox fileBox){
		OwnerDevice ownerDevice = ownerDeviceDao.findFirst("select * from owner_device where device_id = ? and cabinet_num = ? and number = ?",fileBox.getDeviceId(),fileBox.getCabinetNum(),fileBox.getNumber());
		if(ownerDevice != null){
			return ownerDevice.getOwnerId();
		}else{
			return 0;
		}
	}
	
}
