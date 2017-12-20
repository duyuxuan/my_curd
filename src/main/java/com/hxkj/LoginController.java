package com.hxkj;


import com.hxkj.common.constant.Constant;
import com.hxkj.common.interceptor.AuthorityInterceptor;
import com.hxkj.common.util.BaseController;
import com.hxkj.system.model.SysMenu;
import com.hxkj.system.model.SysUser;
import com.hxkj.system.model.SysUserRole;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.log4j.Logger;

import java.util.List;

public class LoginController extends BaseController {

    private final static Logger LOG = Logger.getLogger(LoginController.class);

    /**
     * 登录页面
     */
    @Clear(AuthorityInterceptor.class)
    public void index() {
        render("login.html");
    }

    /**
     * 登录Action
     */
    @Clear(AuthorityInterceptor.class)
    @Before(Tx.class)
    public void action() {

        String username = getPara("username");
        String password = getPara("password");

        if (StrKit.isBlank(username)) {
            setAttr("errMsg", "请填写用户名。");
            render("login.html");
            return;
        }

        if (StrKit.isBlank(password)) {
            setAttr("errMsg", "请填写密码。");
            render("login.html");
            return;
        }
        SysUser sysUser = SysUser.dao.findByUsername(username);
        if (sysUser == null) {
            setAttr("errMsg", username + " 用户不存在。");
            render("login.html");
            return;
        }
        password = HashKit.sha1(password);
        if (!sysUser.getPassword().equals(password)) {
            setAttr("errMsg", " 密码错误。");
            render("login.html");
            return;
        }

        if (sysUser.getDisabled().equals("1")) {
            setAttr("errMsg", username + " 用户被禁用，请联系管理员。");
            render("login.html");
        }
        //登录用户信息
        setSessionAttr(Constant.SYSTEM_USER, sysUser);
        //为了 druid session 监控用
        setSessionAttr(Constant.SYSTEM_USER_NAME, sysUser.getName());

        // 用户角色
        SysUserRole sysUserRole = SysUserRole.dao.findRolesByUserId(sysUser.getId());
        if (sysUserRole != null) {
            // 角色名称（显示用)
            setSessionAttr(Constant.SYSTEM_USER_ROLES, sysUserRole.get("roleNames"));
            LoginService loginService = Duang.duang(LoginService.class);
            List<SysMenu> userMenus = loginService.getUserMenu(sysUserRole.get("roleIds"));
            setSessionAttr(Constant.OWN_MENU, userMenus);
        }

        addOpLog("登录");
        redirect("/main");
    }


    /**
     * 退出
     */
    @Clear(AuthorityInterceptor.class)
    @ActionKey("/logout")
    @Before(Tx.class)
    public void logout() {
        addOpLog("退出");
        removeSessionAttr(Constant.SYSTEM_USER);
        removeSessionAttr(Constant.SYSTEM_USER_ROLES);
        removeSessionAttr(Constant.OWN_MENU);
        redirect("/login");
    }


}