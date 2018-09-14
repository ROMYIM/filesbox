package com.filebox.admin.openphoneadv;

import java.util.ArrayList;
import java.util.List;

import com.filebox.admin.phoneadv.PhoneAdvertisingService;
import com.filebox.common.kit.ImageKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.AdvertisingPhone;
import com.filebox.common.model.AdvertisingPhone;
import com.jfinal.kit.JMap;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;

public class OpenPhoneAdvService {
	public static final OpenPhoneAdvService me = new OpenPhoneAdvService();
	public static final AdvertisingPhone advDao = new AdvertisingPhone();
	public static final String extName = ".jpg";
	
	public List<AdvertisingPhone> list(){
		return advDao.find("select a.*,b.name,c.type_name from advertising_phone a left join operator b on a.operatorId = b.id left join adv_type c on a.adv_typeId = c.id");
	}

	public RetKit listByPage(int ps, int pn, JMap map) {
		JMap sqlMap = createSearchSql(map);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		return RetKit.ok("page", Db.paginate(pn, ps, "select a.*,b.name,c.type_name ", sql, para));
	}

	private JMap createSearchSql(JMap map) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		sb.append("from advertising_phone a ");
		sb.append("left join operator b on a.operatorId = b.id left join adv_type c on a.adv_typeId = c.id ");
		sb.append("where 1=1 ");
		if (StrKit.notBlank(map.getStr("name"))) {
			sb.append("and b.name like '%" + map.getStr("name") + "%' ");
		}
		if (StrKit.notBlank(map.getStr("type_name"))) {
			sb.append("and c.type_name like '%" + map.getStr("type_name") + "%' ");
		}

		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}

	public Object edit(String id) {
		return advDao.findFirst("select * from advertising_phone where id = ? ",id);
	}

	public String getUploadPath() {
		return "/phoneadv/";
	}

	public int getAvatarMaxSize() {
		return 1024 * 1024;
	}

	public void update(AdvertisingPhone model, UploadFile uf) {
		if (uf==null) {
			Db.update("update advertising_phone  set sort = ?,link = ?,operatorId = ?,adv_typeId=?,adv_status=?,informationlink=? where id = ?",
			model.getSort(),model.getLink(),model.getOperatorId(),model.getAdvTypeid(),model.getAdvStatus(),model.getInformationlink(),model.getId());
		}else{
			model.setPhoto(uploadAvatar(uf).get("avatarUrl").toString());
			model.update();
		}
	}

	public RetKit uploadAvatar(UploadFile uf) {
		if (uf == null) {
			return RetKit.fail("msg", "上传文件UploadFile对象不能为null");
		}
		try {
			if (ImageKit.notImageExtName(uf.getFileName())) {
				return RetKit.fail("msg", "文件类型不正确，只支持图片类型：gif、jpg、jpeg、png、bmp");
			}
			String avatarUrl = "/upload/phoneadv/" + System.currentTimeMillis() + extName;
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

	public boolean save(int sort, String link,int operatorId,int adv_typeId,String photo, String textcontent,int adv_status,String informationlink) {
		AdvertisingPhone advModel = new AdvertisingPhone();
		advModel.setSort(sort);
		advModel.setLink(link);
		advModel.setAdvTypeid(adv_typeId);
		advModel.setPhoto(photo);
		advModel.setAdvStatus(adv_status);
		advModel.setInformationlink(informationlink);
		advModel.setTextcontent(textcontent);
		advModel.setOperatorId(operatorId);
		return advModel.save();
	}

	public Ret delete(String id) {
		boolean flag = advDao.deleteById(id);
		if (flag) {
			return Ret.ok("msg", "删除成功");
		}else{
			return Ret.fail("msg", "删除失败");
		}
	}

	public boolean savetext(String textcontent) {
		
		AdvertisingPhone advModel = new AdvertisingPhone();
		advModel.setTextcontent(textcontent);
		return advModel.save();
	}


}
