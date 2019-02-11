package com.spring.common.mvc;

import com.spring.sys.pojo.SysUserInfo;
import org.apache.shiro.SecurityUtils;

/**
 * controller基础类
 *
 * @author bigcatpan
 * @since: V1.0
 * @date 2018/3/2 9:57
 */
public abstract class BaseController extends AbstractLogging {

    protected SysUserInfo getUser() {
        return (SysUserInfo) SecurityUtils.getSubject().getPrincipal();
    }

    protected Integer getUserId() {
        return getUser().getId();
    }

    protected Integer getDeptId() {
        return getUser().getDeptId();
    }
}
