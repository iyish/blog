package com.spring.common.mvc;

import com.spring.common.utils.RedisUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.Serializable;

/**
 * Custom Redis Session
 *
 * @author bigcatpan
 * @date 2018/4/24 14:42
 * @since V1.0
 */
@Component
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RedisUtils redisUtils;

    //创建session
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        if (logger.isDebugEnabled()) {
            logger.debug("创建session:{}", session.getId());
        }
        final String key = sessionId.toString();
        setShiroSession(key, session);
        return sessionId;
    }

    //获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (logger.isDebugEnabled()) {
            logger.debug("获取session:{}", sessionId);
        }
        // 先从缓存中获取session，如果没有再去Redis中获取
        Session session = super.doReadSession(sessionId);
        if (session == null) {
            final String key = sessionId.toString();
            session = getShiroSession(key);
        }
        return session;
    }

    //更新session
    @Override
    protected void doUpdate(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug("更新session:{}", session.getId());
        }
        super.doUpdate(session);
        final String key = session.getId().toString();
        if (redisUtils.hasKey(key)) {
            setShiroSession(key, session);
        }
    }

    //删除session
    @Override
    protected void doDelete(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug("删除session:{}", session.getId());
        }
        super.doDelete(session);
        final String key = session.getId().toString();
        redisUtils.delete(key);
    }

    private Session getShiroSession(String key) {
        return redisUtils.get(key, Session.class);
    }

    private void setShiroSession(String key, Session session) {
        redisUtils.set(key, session);
    }

}
