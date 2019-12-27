package com.danbro.shiro.dynamic.configuration.enity;

import lombok.Data;

/**
 * @Classname User
 * @Description TODO
 * @Date 2019/12/26 13:11
 * @Author Danrbo
 */
@Data
public class User {

    private Integer id;
    private String username;
    private String password;
    private String perms;
    private String role;
}
