package com.filebox.admin.bonuspoints;

import com.filebox.common.model.BonusPoints;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class BonusPointsService {

	public static final BonusPointsService me = new BonusPointsService();
	static final BonusPoints bonusPointsDao = new BonusPoints().dao();
	
	public BonusPointsService() {
		// TODO Auto-generated constructor stub
	}

	public Page<Record> getTotalBonusPointsRecord(int pn, int ps) {
		return Db.paginate(pn, ps, "select id, phone, points ", "from file_box_owner where points >= 0");
	}
	
	public Page<Record> getUserBonusPointsRecord(int pn, int ps, int ownerId) {
		return Db.paginate(pn, ps, "select * ", "from bonus_points where owner_id = ?", ownerId);
	}
}
