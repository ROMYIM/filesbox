package com.filebox.api.owner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;

import com.alibaba.druid.support.logging.Log;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.FileBoxOwner;
import com.filebox.common.model.OwnerDetail;
import com.filebox.common.model.OwnerDevice;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * @Description:TODO(用户管理)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月4日
 */
public class ApiOwnerService {
	public static final ApiOwnerService me = new ApiOwnerService();
	static final FileBoxOwner fileBoxOwnerDao = new FileBoxOwner().dao();
	static final OwnerDetail ownerDetailDao = new OwnerDetail().dao();

	public RetKit updateOwnerStatus(int ownerId, int status) {
		FileBoxOwner fileBoxOwner = fileBoxOwnerDao.findFirst("select * from file_box_owner where id = ?", ownerId);
		fileBoxOwner.setStatus(status);
		boolean succ = fileBoxOwner.update();

		return succ ? RetKit.ok() : RetKit.fail("更改失败");
	}

	public RetKit updateOwnerDeviceStatus(int ownerId, String deviceId, int status) {
		OwnerDetail ownerDetail = ownerDetailDao
				.findFirst("select * from owner_detail where owner_id = ? and device_id = ?", ownerId, deviceId);
		ownerDetail.setOwnerDeviceStatus(status);
		boolean succ = ownerDetail.update();

		return succ ? RetKit.ok() : RetKit.fail("更改失败");
	}

	public RetKit deleteOwner(int ownerId) {
		int n = Db.update("delete from file_box_owner where id = ?", ownerId);
		if (n > 0) {
			return RetKit.ok("msg", "删除成功");
		} else {
			return RetKit.fail("msg", "删除失败");
		}
	}

	public RetKit searchOwner(String deviceId, String searchValue, int pagePize, int pageNum) {
		Page<Record> list = Db.paginate(pageNum, pagePize, "select *,count(b.id) as device_count",
				"from owner_device a inner join file_box_owner b on"
						+ " a.owner_id = b.id and a.device_id = ? and phone like '%" + searchValue
						+ "%' group by a.owner_id",
				deviceId);
		list.getList().remove("password");
		list.getList().remove("password_card");
		list.getList().remove("password_fingerprint");
		return RetKit.ok("data", list);
	}

	/**
	 ** 获取排除已经存在的指纹数的随机数
	 */
	public int getRandom(int min, int max, List<Integer> list) {
		HashSet<Integer> hashSetAll = new HashSet<Integer>();
		for (int i = min; i <= max; i++) {
			hashSetAll.add(i);
		}
		Iterator iterator = hashSetAll.iterator();
		while (iterator.hasNext()) {
			Integer value = (Integer) iterator.next();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(value)) {
					iterator.remove();
				}
			}
		}
		System.out.println("hashSetAll 大小：" + hashSetAll.size());
		List<Integer> finalList = new ArrayList<Integer>(hashSetAll);
		return finalList.get((int) (Math.random() * finalList.size() - 1));
	}

	/**
	 * 
	 * 获取一个该设备指纹不重复随机数
	 */
	public RetKit getFingerPrintRandom(String deviceId) {
		List<OwnerDetail> details = getOwnerFingerPrintList(deviceId);
		List<Integer> existList = new ArrayList<Integer>();
		if (details != null) {
			for (OwnerDetail item : details) {
				if (StrKit.notNull(item.getPasswordFingerprint()) && !item.getPasswordFingerprint().equals("")) {
					existList.add(Integer.parseInt(item.getPasswordFingerprint()));
				}
			}
		}

		int ran = getRandom(1, 12, existList);
		if (ran > 0) {
			return RetKit.ok("data", ran);
		} else {
			return RetKit.fail("msg", "获取随机数失败");
		}
	}

	/**
	 * 获取指定设备用户指纹id列表
	 */
	public List<OwnerDetail> getOwnerFingerPrintList(String deviceId) {
		return ownerDetailDao.find("select * from owner_detail where device_id = ?", deviceId);
	}

}
