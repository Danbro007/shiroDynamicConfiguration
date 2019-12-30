package com.danbro.shiro.dynamic.configuration.enity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname User
 * @Description TODO
 * @Date 2019/12/26 13:11
 * @Author Danrbo
 */
@Data
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String perms;

    private String role;

    private String salt;

    public String getSalt() {
        return username + salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
