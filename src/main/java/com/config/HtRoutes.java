package com.config;

import com.controller.ht.IndexController;
import com.controller.ht.ZQuestionController;
import com.jfinal.config.Routes;
import com.plugin.kindedtor.controller.KindeditorController;

/**
 * @describe 后台路由
 * @author zj
 * 
 */
public class HtRoutes extends Routes {

	@Override
	public void config() {
		add("/", IndexController.class);
		add("/question", ZQuestionController.class);
	}
}
