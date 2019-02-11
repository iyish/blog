package com.spring.common.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author bigcatpan
 * @date 2018/4/24 13:48
 * @since V1.0
 */
@Component
public class RedisUtils {
    @Autowired
    private  RedisTemplate<String, String> redisTemplate;
    private  ValueOperations<String, String> vOps;
    /**
     * 默认过期时长,单位秒
     */
    private final  long DEFAULT_EXPIRE = 60 * 60;
    /**
     * 不设置过期时长
     */
    private final  long NOT_EXPIRE = -1;

    /**
     * 设置缓存kv和有效期
     *
     * @param key
     * @param value
     */
    public  void set(final String key, Object value, long expire) {
        vOps.set(key, toJsonString(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置缓存kv
     *
     * @param key
     * @param value
     */
    public  void set(final String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    /**
     * 获取缓存值
     *
     * @param key
     * @param clazz
     */
    public  <T> T get(final String key, Class<T> clazz) {
        String value = vOps.get(key);
        return value == null ? null : fromJson(value, clazz);
    }


    public  String get(final String key) {
        return vOps.get(key);
    }

    public  void delete(final String key) {
        redisTemplate.delete(key);
    }

    public  boolean hasKey(final String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * Object转成JSON字符串
     */
    private  String toJsonString(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * JSON字符串解析成Object
     */
    private  <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}
