package com.spring.common.shiro;

import com.spring.common.utils.Constants;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置类
 *
 * @author bigcatpan
 * @date 2018/3/5 14:08
 * @since V1.0
 */
@Configuration
public class ShiroConfig {

    /**
     * cookie
     * @return simpleCookie
     */
    @Bean("simpleCookie")
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("sid");
        simpleCookie.setHttpOnly(true);
        // 30天过期
        simpleCookie.setMaxAge(30 * 24 * 60 * 60);
        return simpleCookie;
    }



    /**
     * shiro redis cache manager
     * @param redisManager redis manager
     * @return redisCacheManager
     */
    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    @Bean("ehcacheSessionDao")
    public EnterpriseCacheSessionDAO ehcacheSessionDao(EhCacheManager shiroEhCacheManager) {
        EnterpriseCacheSessionDAO ehcacheSessionDao = new EnterpriseCacheSessionDAO();
        ehcacheSessionDao.setCacheManager(shiroEhCacheManager);
        ehcacheSessionDao.setActiveSessionsCacheName("shiro-session-cache");
        return ehcacheSessionDao;
    }

    /**
     * session redis cache
     * 由第三方实现,也可自行实现,参见{@link com.spring.common.mvc.RedisSessionDAO}
     * @param redisManager redis管理
     * @return RedisSessionDAO
     */
    @Bean("redisSessionDAO")
    public RedisSessionDAO redisSessionDAO(RedisManager redisManager) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        return redisSessionDAO;
    }

    @Bean("sessionManager")
    public SessionManager sessionManager(SimpleCookie simpleCookie,
                                         EnterpriseCacheSessionDAO ehcacheSessionDao,
                                         RedisSessionDAO redisSessionDAO,
                                         @Value("${shiro.cache.type}") int cacheType) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //设置session过期时间为1小时(单位：毫秒)，默认30分钟
        sessionManager.setGlobalSessionTimeout(60 * 60 * 1000);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        //启用定时器,定期检测会话
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(60 * 60 * 1000);
        //创建会话Cookie
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(simpleCookie);
        //ehcache存储session,默认存储
        if (Constants.ShiroCacheType.EHCACHE.getValue() == cacheType) {
            sessionManager.setSessionDAO(ehcacheSessionDao);
        }
        //redis存储session
        else if (Constants.ShiroCacheType.REDIS.getValue() == cacheType) {
            sessionManager.setSessionDAO(redisSessionDAO);
        }
        return sessionManager;
    }

    @Bean("securityManager")
    public SecurityManager securityManager(UserRealm userRealm,
                                           SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //缓存userRealm
//        userRealm.setCachingEnabled(true);
//        userRealm.setAuthenticationCachingEnabled(true);
//        userRealm.setAuthenticationCacheName("shiro-userRealm");
//        userRealm.setAuthorizationCachingEnabled(true);
//        userRealm.setAuthorizationCacheName("shiro-userRealm");
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login.html");
        shiroFilter.setUnauthorizedUrl("/");
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/statics/**", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/login.html", "anon");
        filterMap.put("/header.html", "anon");
        filterMap.put("/footer.html", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/captcha", "anon");
        filterMap.put("/favicon.ico", "anon");
        filterMap.put("/**", "authc");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
