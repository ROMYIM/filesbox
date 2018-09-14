package com.filebox.admin.bonuspoints;

import com.jfinal.core.Controller;

public class BonusPointsController extends Controller {

	static final BonusPointsService srv = BonusPointsService.me;
	
	public BonusPointsController() {
		// TODO Auto-generated constructor stub
	}
	
	public void index() {
		int pageNum = getParaToInt("pageNum", 1);
		int pageSize = getParaToInt("pageSize", 10);
		setAttr("page", srv.getTotalBonusPointsRecord(pageNum, pageSize));
		render("index.html");
	}
	
	public void getUserDetail() {
		int ownerId = getParaToInt(0);
		int totalPoints = getParaToInt(1);
		int pageNum = getParaToInt("pageNum", 1);
		int pageSize = getParaToInt("pageSize", 10);
		setAttr("totalPoints", totalPoints);
		setAttr("page", srv.getUserBonusPointsRecord(pageNum, pageSize, ownerId));
		render("detail.html");
	}
}
