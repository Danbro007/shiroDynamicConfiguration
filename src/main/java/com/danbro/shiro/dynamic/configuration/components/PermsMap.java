package com.danbro.shiro.dynamic.configuration.components;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Classname PermsMap
 * @Description TODO
 * @Date 2019/12/27 12:04
 * @Author Danrbo
 */
@Data
@Component
@ConfigurationProperties(prefix = "permisson-config")
public class PermsMap {
    private List<Map<String,String>> perms;
    private String test;
}
