package com.danbro.shiro.dynamic.configuration.utils;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * @Classname PasswordGenerateUtil
 * @Description TODO 产生加密后的密码
 * @Date 2019/12/30 13:23
 * @Author Danrbo
 */
public class PasswordGenerateUtil {
    /**
     *
     * @param username 用户名
     * @param password 输入的密码
     * @param salt 盐值
     * @param hashTime 散列次数 与之前在ShiroConfig里HashedCredentialsMatcher的设置要一致
     * @return 加密后的密码
     */
    public static String getPassword(String username, String password, String salt, int hashTime) {
        return new Md5Hash(password, username + salt, hashTime).toString();
    }
}
