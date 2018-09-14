package com.filebox.admin.information;

import java.util.ArrayList;
import java.util.List;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Information;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

public class InformationService {
	
	public static final InformationService me =new InformationService();
	private static final Information dao=new Information();
	
	
	public List<Information> list(){
		return dao.find("select a.*,b.name from information a left join operator b on a.operatorId = b.id");
	}

	public static List<Information> titlelist(){
		return dao.find("select title from information");
	}

	
	public RetKit listByPage(int ps, int pn, JMap map) {
		JMap sqlMap = createSearchSql(map);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		return RetKit.ok("page", Db.paginate(pn, ps, "select a.*,b.name ", sql, para));
	}

	private JMap createSearchSql(JMap map) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		sb.append("from information a ");
		sb.append("left join operator b on a.operatorId = b.id ");
		sb.append("where 1=1 ");
		if (StrKit.notBlank(map.getStr("name"))) {
			sb.append("and b.name like '%" + map.getStr("name") + "%' ");
		}

		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}

	public RetKit delete(String id) {
		boolean b=dao.deleteById(id);
		if(b){
			return RetKit.ok("msg","删除成功");
		}else{
			return RetKit.fail("msg", "删除失败");
		}
	}

	public Object  edit(String id) {
		return dao.findFirst("select * from information where id = ? ",id);
	}

	public boolean save(String content,String title,int sort) {
		Information inforModel = new Information();
		inforModel.setTitle(title);
		inforModel.setSort(sort);
		inforModel.setContent(content);
		return inforModel.save();
		
	}

	public void update(Information model) {
		Db.update("update information set sort = ?,title = ? where id = ?",
				model.getSort(),model.getTitle(),model.getId());
		
	}



}
