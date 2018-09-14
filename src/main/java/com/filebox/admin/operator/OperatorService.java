package com.filebox.admin.operator;

import java.util.List;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Operator;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.xml.internal.bind.v2.model.core.Adapter;

/**
 * @Description:TODO(运营商服务层)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月16日
 */
public class OperatorService {
	public static final OperatorService me = new OperatorService();
	static Operator operatorDao = new Operator().dao();

	/**
	 * 运营商列表
	 */
	public static List<Operator> operatorList() {
		return operatorDao
				.find("select a.*,(select count(*) from file_device b where a.id = b.operator_id) as device_num "
						+ "from operator a ");
	}

	public Operator getOperatorInfo(int id) {
		return operatorDao.findById(id);
	}

	public RetKit addOperator(Operator model) {
		return model.save() ? RetKit.ok() : RetKit.fail();
	}

	public RetKit updateOperator(Operator model) {
		return model.update() ? RetKit.ok() : RetKit.fail();
	}

	public RetKit delete(int operatorId) {
		Record record = Db.findFirst("select * from file_device where operator_id = ? limit 1", operatorId);
		if (record != null) {
			return RetKit.fail("此运营商还有运营的设备，无法删除");
		}
		Operator operator = new Operator();
		operator.setId(operatorId);
		return operator.delete() ? RetKit.ok() : RetKit.fail();
	}
}
