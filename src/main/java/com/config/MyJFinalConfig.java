package com.config;

import com.interceptor.GlobalInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.model.TArticle;
import com.utils.ZJ_PropertyConfig;

public class MyJFinalConfig extends JFinalConfig {
	boolean devMode = false;
	boolean isUseOracle = false;
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载数据库配置文件		
		ZJ_PropertyConfig config = ZJ_PropertyConfig.me();
		config.loadPropertyFile("config.properties");
		
		devMode = config.getPropertyToBoolean("devMode", false);
		isUseOracle = config.getPropertyToBoolean("isUseOracle", false);
		// 设定为开发者模式
		me.setDevMode(devMode);
		me.setViewType(ViewType.FREE_MARKER);
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add(new HtRoutes()); // 后台路由
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {

	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new GlobalInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
	}

}
