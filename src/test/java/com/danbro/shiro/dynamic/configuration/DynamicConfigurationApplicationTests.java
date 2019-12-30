package com.danbro.shiro.dynamic.configuration;

import cn.hutool.crypto.SecureUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamicConfigurationApplicationTests {

    @Test
    public void contextLoads() {
        String s = SecureUtil.md5("123");
        System.out.println(s);
    }
}
