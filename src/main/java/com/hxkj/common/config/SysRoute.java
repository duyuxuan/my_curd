package com.hxkj.common.config;


import com.hxkj.common.constant.Constant;
import com.hxkj.sys.controller.*;
import com.jfinal.config.Routes;


/**
 * 系统主模块 路由
 *
 * @author zhangchuang
 */
public class SysRoute extends Routes {

    @Override
    public void config() {
        // 系统日志
        add("/sysOplog", sysOplogController.class, Constant.VIEW_PATH);
        // 代码生成器
        add("/sysGenerator", SysGeneratorController.class, Constant.VIEW_PATH);

    }

}
