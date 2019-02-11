package com.spring.common.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

/**
 * shiro权限标签
 * @author bigcatpan
 * @since: V1.0
 * @date 2018/3/2 15:05
 */
@Component
public class ShiroTag {
    public boolean hasPermission(String permission) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isPermitted(permission);
    }
}
