package com.filebox.admin.adv;

import java.util.ArrayList;
import java.util.List;

import com.filebox.admin.login.LoginService;
import com.filebox.common.kit.ImageKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Account;
import com.filebox.common.model.Advertising;
import com.jfinal.kit.JMap;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;

/**
* @Description:TODO(广告服务)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月25日
*/
public class AdvertisingService {
	public static final AdvertisingService me = new AdvertisingService();
	private static final Advertising advDao = new Advertising();
	// 经测试对同一张图片裁切后的图片 jpg为3.28KB，而 png 为 33.7KB，大了近 10 倍
	public static final String extName = ".jpg";
	

	public List<Advertising> list(){
		return advDao.find("select a.*,b.name from advertising a left join operator b on a.operatorId = b.id");
	}
	
	public RetKit listByPage(int ps, int pn, JMap map) {
		JMap sqlMap = createSearchSql(map);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		return RetKit.ok("page", Db.paginate(pn, ps, "select a.*,b.name ", sql, para));
	}
	
	public Advertising edit(String id){
		return advDao.findFirst("select * from advertising where id = ? ",id);
	}

	public boolean save(int sort,int operatorId, String photo){
		Advertising advModel = new Advertising();
		advModel.setSort(sort);
		advModel.setPhoto(photo);
		advModel.setOperatorId(operatorId);
		return advModel.save();
	}
	
	public void update(Advertising model,UploadFile uf){
		if (uf==null) {
			Db.update("update advertising set sort = ?,operatorId = ? where id = ?",
					model.getSort(),model.getOperatorId(),model.getId());
		}else{
			model.setPhoto(uploadAvatar(uf).get("avatarUrl").toString());
			model.update();
		}
	}
	
	public Ret delete(String id){
		boolean flag = advDao.deleteById(id);
		if (flag) {
			return Ret.ok("msg", "删除成功");
		}else{
			return Ret.fail("msg", "删除失败");
		}
	}

	
	/**
	 * 上传图像
	 */
	public RetKit uploadAvatar(UploadFile uf) {
		if (uf == null) {
			return RetKit.fail("msg", "上传文件UploadFile对象不能为null");
		}
		try {
			if (ImageKit.notImageExtName(uf.getFileName())) {
				return RetKit.fail("msg", "文件类型不正确，只支持图片类型：gif、jpg、jpeg、png、bmp");
			}
			String avatarUrl = "/upload/adv/" + System.currentTimeMillis() + extName;
			String saveFile = PathKit.getWebRootPath() + avatarUrl;
			ImageKit.zoom(500, uf.getFile(), saveFile);
			return RetKit.ok("avatarUrl", avatarUrl);
		}
		catch (Exception e) {
			return RetKit.fail("msg", e.getMessage());
		} finally {
			uf.getFile().delete();
		}
	}
	
	// 用户上传图像最多只允许 1M大小
	public int getAvatarMaxSize() {
		return 1024 * 1024;
	}
	
	public String getUploadPath(){
		return "/adv/";
	}
	
	
	private JMap createSearchSql(JMap map) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		sb.append("from advertising a ");
		sb.append("left join operator b on a.operatorId = b.id ");
		sb.append("where 1=1 ");
		if (StrKit.notBlank(map.getStr("name"))) {
			sb.append("and b.name like '%" + map.getStr("name") + "%' ");
		}

		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}

}
