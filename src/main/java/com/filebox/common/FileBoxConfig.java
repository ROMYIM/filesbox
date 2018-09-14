package com.filebox.common;

import com.filebox.common.handler.WebSocketHandler;
import com.filebox.common.model._MappingKit;
import com.filebox.common.routes.AdminRoutes;
import com.filebox.common.routes.ApiRoutes;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;

/**
 * @Description:TODO(JFinal 主控制类)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年4月11日
 */
public class FileBoxConfig extends JFinalConfig {

	private static Prop p = loadConfig();

	@Override
	public void configConstant(Constants me) {
		// 读取配置文件
		PropKit.use("config.properties");
		// 设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		// 设置默认上传文件保存路径getFile等使用
		// me.setBaseUploadPath("upload/temp/");
		// 设置上传最大限制尺寸
		me.setMaxPostSize(1024 * 1024 * 10);
		// 设置默认下载文件路径renderFile使用
		// me.setBaseDownloadPath("");
		// 设置默认视图类型
		// me.setViewType(ViewType.JSP);
		// 设置404渲染视图
		me.setError404View("/404.html");
		// 设置第三方FastJson
		me.setJsonFactory(MixedJsonFactory.me());
		// 设置返回JSON时间格式
		me.setJsonDatePattern("yyyy-MM-dd HH:mm:ss");

	}

	private static Prop loadConfig() {
		try {
			// 优先加载生产环境配置文件
			return PropKit.use("filebox_config_pro.txt");
		} catch (Exception e) {
			// 找不到生产环境配置文件，再去找开发环境配置文件
			return PropKit.use("config.properties");
		}
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new AdminRoutes());
		me.add(new ApiRoutes());
	}

	@Override
	public void configEngine(Engine me) {
		me.addSharedFunction("/_view/common/_layout.html");
    	me.addSharedFunction("/_view/common/_menu.html");
    	me.addSharedFunction("/_view/common/_admin_menu.html");
    	me.addSharedFunction("/_view/common/_paginate.html");
    	me.addSharedObject("sk", new com.jfinal.kit.StrKit());
//    	me.addSharedMethod(new TemplateKit());

	}

	@Override
	public void configPlugin(Plugins me) {
		// 配置数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		// orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setShowSql(p.getBoolean("devMode"));
		arp.setDialect(new MysqlDialect());
		/******** 在此添加数据库 表-Model 映射 *********/
		_MappingKit.mapping(arp);

		// TODO
		// 添加到插件列表中
		me.add(druidPlugin);
		me.add(new EhCachePlugin());// 缓存
		// me.add(new Cron4jPlugin(p)); //定时调度
		me.add(arp);
	}

	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub

	}

	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("filesbox"));
		me.add(new WebSocketHandler("/websocket"));
	}

	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/filesbox", 9999999);
	}
}
