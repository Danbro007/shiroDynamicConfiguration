package com.danbro.shiro.dynamic.configuration.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.danbro.shiro.dynamic.configuration.components.PermsMap;
import com.danbro.shiro.dynamic.configuration.realm.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Classname ShiroConfig
 * @Description TODO shrio配置类
 * @Date 2019/12/26 10:31
 * @Author Danrbo
 */
@Configuration
public class ShiroConfig {

    @Autowired
    PermsMap permsMap;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private Integer port;

    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("webSecurityManager")DefaultWebSecurityManager webSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //配置webSecurityManager
        shiroFilterFactoryBean.setSecurityManager(webSecurityManager);
        /**
         * shiro内置的过滤器，可以实现权限相关的拦截器
         * 常用的过滤器
         * anon:无需认证（登录）可以访问
         * authc:必须认证才可以访问
         * user;如果需要rememberMe的功能可以访问
         * perms:该资源必须得到资源权限才可以访问
         * role:该资源必须得到角色权限才可以访问
         *
         */
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
        //设置/user/*的页面都需要认证才能登陆
        //认证好的用户无法访问add页面，并且会自动跳转到设置好的unauth页面，perms[user:add]里的perms表示权限，user表示user这个用户 add表示add这个行为
        filterMap.put("/user/*","authc");
        //把在配置文件tml读取到的权限放到filterMap中
        List<Map<String, String>> perms = permsMap.getPerms();
        perms.forEach(perm->filterMap.put(perm.get("url"),perm.get("permission")));
        //配置登录页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        //设置未授权页面 与行为相关
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "webSecurityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("defaultWebSessionManager") DefaultWebSessionManager defaultWebSessionManager,
                                                                  @Qualifier("redisCacheManager") RedisCacheManager redisCacheManager,
                                                                  @Qualifier("cookieRememberMeManager")RememberMeManager rememberMeManager,
                                                                  @Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        //配置realm
        webSecurityManager.setRealm(userRealm);
        //配置缓存管理器
        webSecurityManager.setCacheManager(redisCacheManager);
        //配置session管理器
        webSecurityManager.setSessionManager(defaultWebSessionManager);
        //配置rememberMe管理器
        webSecurityManager.setRememberMeManager(rememberMeManager);
        return webSecurityManager;
    }


    /**
     * 配置ShiroDialect，用于thymeleaf和shiro标签配合使用
     * @return
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }

    /**
     * 开启aop注解支持
     * 即在controller中使用 @RequiresPermissions("user/add")
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(@Qualifier("webSecurityManager")DefaultWebSecurityManager webSecurityManager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(webSecurityManager);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 设置角色没有权限的错误页面 与角色相关
     * @return 解析器
     */
    @Bean
    public SimpleMappingExceptionResolver getSimpleMappingExceptionResolver(){
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty("UnauthorizedException", "/unauthorized");
        resolver.setExceptionMappings(properties);
        return resolver;
    }

    /**
     * 创建redis管理器，配置端口地址密码等参数
     * @return redis管理器
     */
    @Bean(name = "redisManager")
    public RedisManager getRedisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        return redisManager;
    }

    /**
     * 创建redis缓存管理器
     * @param redisManager redis缓存管理器
     * @return redis缓存管理器
     */
    @Bean(name = "redisCacheManager")
    public RedisCacheManager getCacheManager(@Qualifier("redisManager")RedisManager redisManager){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    /**
     * 创建redisSessionDao，并且设置redis管理器
     * @param redisManager redis管理器
     * @return redisSessionDAO
     */
    @Bean(name = "redisSessionDAO")
    public RedisSessionDAO getRedisSessionDAO(@Qualifier("redisManager") RedisManager redisManager){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        return redisSessionDAO;
    }

    /**
     * 创建session管理器，并设置sessionDAO
     * @param redisSessionDAO  redisSessionDAO
     * @return session管理器
     */
    @Bean(name = "defaultWebSessionManager")
    public DefaultWebSessionManager getDefaultWebSessionManager(@Qualifier("redisSessionDAO") RedisSessionDAO redisSessionDAO){
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionDAO(redisSessionDAO);
        return defaultWebSessionManager;
    }

    /**
     * 创建cookie，并且设置cookie超时时间和cookie名
     * @return cookie对象
     */
    @Bean(name = "rememberMeCookie")
    public SimpleCookie getRememberMeCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("rememberMeCookie");
        //设置超时时间
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * 创建记住我管理器
     * @param rememberMeCookie cookie对象
     * @return 记住我管理器
     */
    @Bean(name = "cookieRememberMeManager")
    public CookieRememberMeManager cookieRememberMeManager(@Qualifier("rememberMeCookie")SimpleCookie rememberMeCookie){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie);
        return cookieRememberMeManager;
    }

    /**
     * 创建realm
     * @param credentialsMatcher 加密算法设置
     * @return realm对象
     */
    @Bean(name = "userRealm")
    public UserRealm getUserRealm(@Qualifier("hashedCredentialsMatcher")HashedCredentialsMatcher credentialsMatcher ){
        UserRealm userRealm = new UserRealm();

        userRealm.setCredentialsMatcher(credentialsMatcher);
        return userRealm;
    }

    /**
     * 设置凭据匹配器的算法和散列次数
     * @return
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher getHashedCredentialsMatcher(){
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置加密方式
        matcher.setHashAlgorithmName("md5");
        //设置散列次数
        matcher.setHashIterations(2);
        return matcher;
    }

}
