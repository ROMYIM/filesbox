package com.filebox.admin.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.filebox.common.kit.ImageKit;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.Model;
import com.filebox.common.model.Protocol;
import com.jfinal.kit.JMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

/**
* @Description:TODO(协议)
* @author 作者 : jinghui.su
* @date 创建时间：2017年6月26日
*/
public class ProtocolService {
	public static final ProtocolService me = new ProtocolService();
	static final Protocol protocolDao = new Protocol().dao();
	
	/**
	 * 分页获取协议
	 */
	public RetKit listByPage(int ps, int pn, JMap map){
		JMap sqlMap = createSearchSql(map);
		String sql = sqlMap.getStr("sql");
		Object[] para = (Object[]) sqlMap.get("para");
		return RetKit.ok("page", Db.paginate(pn, ps, "select * ", sql, para));
	}
	
	private JMap createSearchSql(JMap map) {
		StringBuilder sb = new StringBuilder();
		List<Object> paraList = new ArrayList<>();
		sb.append("from protocol ");
		sb.append("where 1=1 ");
		if (StrKit.notBlank(map.getStr("name"))) {
			sb.append("and name like '%" + map.getStr("name") + "%' ");
		}

		return JMap.create().set("sql", sb.toString()).set("para", paraList.toArray());
	}
	
	
	public Protocol getProtocolInfo(int protocolId) {
		return protocolDao.findById(protocolId);
	}
	
	public RetKit addProtocol(Protocol protocol){
		protocol.setSysupdate(new Date());
		boolean success = protocol.save();
		if(success){
			return RetKit.ok("新增成功");
		}else{
			return RetKit.fail("新增失败");
		}
	}
	
	public RetKit updateProtocol(Protocol protocol){
		protocol.setSysupdate(new Date());
		boolean success = protocol.update();
		if(success){
			return RetKit.ok("新增成功");
		}else{
			return RetKit.fail("新增失败");
		}
	}
	
	public RetKit deleteProtocol(int protocolId){
		Protocol protocol = protocolDao.findById(protocolId);
		if(!StrKit.isBlank(protocol.getContent())){
			String content = protocol.getContent();
			if(content.contains(".jpg")){
				List<String> picUrl = searchAllIndexAndReturnPicNameList(content,".jpg");
				if(picUrl != null){
					for(int i = 0 ; i < picUrl.size();i++){
						ImageKit.deleteAvatar("ueditor", picUrl.get(i)+".jpg");
						
					}
				}
			}
		}
	
		int count = Db.update("delete from protocol where id = ?",protocolId);
		if (count>0) {
			return RetKit.ok("删除成功");
		}
		return RetKit.fail("删除失败");
	}
	
	private List<String> searchAllIndexAndReturnPicNameList(String str,String key) {
		List<String> listPicName = new ArrayList<String>();
		List<Integer> listIndex = new ArrayList<Integer>();
		int index = str.indexOf(key);//*第一个出现的索引位置
        while (index != -1) {
            System.out.print(index + "\t");
            listIndex.add(index);
            index = str.indexOf(key, index + 1);//*从这个索引往后开始第一个出现的位置
        }
        if(listIndex.size() > 0){
        	for(Integer item:listIndex){
        		listPicName.add(str.substring(item-14,item));  //图片名字长度为13加一个点
        	}
        }
        return listPicName;
	}	 
	
}
