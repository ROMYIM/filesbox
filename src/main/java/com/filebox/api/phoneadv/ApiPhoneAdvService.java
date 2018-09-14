package com.filebox.api.phoneadv;

import java.util.ArrayList;
import java.util.List;

import com.filebox.common.model.AdvertisingPhone;


public class ApiPhoneAdvService {
	
	public static final AdvertisingPhone dao = new AdvertisingPhone().dao();
	

	public List<AdvertisingPhone> getList() {
		List<AdvertisingPhone> list = new ArrayList<AdvertisingPhone>();
		list = dao.find("select * from advertising_phone where adv_status=1");
		return list;
	}
	
	public AdvertisingPhone getOpenList(){
		AdvertisingPhone AdvertisingPhone=dao.findFirst("select * from advertising_phone Where adv_status=1 Order By sysupdate Desc limit 1");
		return AdvertisingPhone;
	}

}
