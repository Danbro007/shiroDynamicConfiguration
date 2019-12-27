package com.danbro.shiro.dynamic.configuration.service;

import com.danbro.shiro.dynamic.configuration.enity.User;

/**
 * @Classname UserService
 * @Description TODO
 * @Date 2019/12/26 13:15
 * @Author Danrbo
 */
public interface UserService {
    /**
     * 通过用户名在数据库中查找用户
     * @param username 用户名
     * @return 查找到的用户对象
     */
    User selectUserByUsername(String username);

}
