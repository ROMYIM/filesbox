package com.filebox.admin.advtype;

import java.util.List;


import com.filebox.common.model.AdvType;
/**
 * @Description:TODO(广告类型服务层)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月16日
 */
public class AdvtypeService {
	public static final AdvtypeService me = new AdvtypeService();
	static AdvType advtypeDao = new AdvType().dao();

	/**
	 * 广告类型列表
	 */
	public static List<AdvType> advtypeList() {
		return advtypeDao
				.find("select a.* "
						+ "from adv_type a ");
	}

	public AdvType getAdvTypeInfo(int id) {
		return advtypeDao.findById(id);
	}
}
