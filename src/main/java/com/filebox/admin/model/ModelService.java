package com.filebox.admin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Model;
import com.filebox.common.model.ModelDetail;
import com.jfinal.aop.Before;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import net.sf.ehcache.search.parser.MOrderBy;

/**
* @Description:TODO(模型服务)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月25日
*/
public class ModelService {
	public static final ModelService me = new ModelService();
	static final Model modelDao = new Model().dao();
	static final ModelDetail modelDetailDao = new ModelDetail().dao();
	
	public List<Model> modelList(){
		return modelDao.find("select * from model");
	}
	
	/**
	 * 分页获取模型
	 */
	public RetKit listByPage(int ps, int pn, JMap map){
		JMap sqlMap = createSearchSql(map);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		return RetKit.ok("page", Db.paginate(pn, ps, "select * ", sql, para));
	}
	
	public Model getModelInfo(int modelId){
		return modelDao.findById(modelId);
	}
	
	public List<ModelDetail> getModelDetail(int modelId){
		return modelDetailDao.find("select * from model_detail where model_id = ? order by box_num",modelId);
	}
	
	public RetKit updateModel(Model model,Integer[] boxSpecs,Integer[] boxFloor){
		boolean success = model.update();
		if(!success){
			return RetKit.fail("保存型号失败");
		}
		//先删除旧箱子
		Db.update("delete from model_detail where model_id = ?",model.getId());
		List<ModelDetail> modelDetails = new ArrayList<ModelDetail>();
		for(int i = 0 ; i <boxSpecs.length;i++){
			ModelDetail mDetail = new ModelDetail();
			mDetail.setModelId(model.getId());
			mDetail.setBoxNum(i+1);
			mDetail.setBoxSpec(boxSpecs[i]);
			mDetail.setBoxFloor(boxFloor[i]);
			modelDetails.add(mDetail);
		}
		Db.batchSave(modelDetails, modelDetails.size());
		return RetKit.ok();
	}
	
	@Before(Tx.class)
	public RetKit addModel(Model model, Integer[] boxSpecs, Integer[] boxfloor) {
		model.setCreateTime(new Date());
		boolean success = model.save();
		if (!success) {
			return RetKit.fail("保存型号失败");
		}
		List<ModelDetail> modelDetail = new ArrayList<ModelDetail>();
		for (int i = 0; i < boxSpecs.length; i++) {
			ModelDetail md = new ModelDetail();
			md.setModelId(model.getId());
			md.setBoxNum(i+1);
			md.setBoxSpec(boxSpecs[i]);
			md.setBoxFloor(boxfloor[i]);
			modelDetail.add(md);
		}
		Db.batchSave(modelDetail, modelDetail.size());
		return RetKit.ok();
	}
	
	public boolean delete(int id){
		int count = Db.update("delete from model where id = ?",id);
		if (count>0) {
			Db.update("delete from model_detail where model_id = ?",id);
		}
		return count>0;
	}
	
	private JMap createSearchSql(JMap map) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		sb.append("from model ");
		sb.append("where 1=1 ");
		if (StrKit.notBlank(map.getStr("name"))) {
			sb.append("and name like '%" + map.getStr("name") + "%' ");
		}

		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}
	
}
