package com.danbro.shiro.dynamic.configuration.realm;

import com.danbro.shiro.dynamic.configuration.enity.User;
import com.danbro.shiro.dynamic.configuration.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Classname UserRealm
 * @Description TODO
 * @Date 2019/12/26 10:33
 * @Author Danrbo
 */


public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    /**
     * 执行授权逻辑
     *
     * @param principalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        //添加角色
        info.addRole(user.getRole());
        //添加授权
        info.addStringPermission(user.getPerms());
        return info;
    }

    /**
     * 执行认证逻辑
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        //用户名判断是否存在
        User user = userService.selectUserByUsername(username);

        if (user == null) {
            //shiro底层会抛出UnknownAccountException异常
            return null;
        }
        //判断密码是否存在
        return new SimpleAuthenticationInfo(user, user.getPassword(), username);
    }
}
