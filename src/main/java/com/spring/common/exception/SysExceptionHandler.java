package com.spring.common.exception;

import com.spring.common.mvc.AbstractLogging;
import com.spring.common.shiro.ShiroUtils;
import com.spring.common.utils.Result;
import com.spring.sys.pojo.SysExpInfo;
import com.spring.sys.service.SysExpService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Date;
/**
 * @Title: SysExceptionHandler
 * @Description: 统一异常处理类
 * @author bigcatpan
 * @date 2018/9/9 15:42
 * @since v1.0
 */
@RestControllerAdvice
public class SysExceptionHandler extends AbstractLogging {
    @Autowired
    SysExpService sysExpService;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(SysException.class)
    public Result handleSysException(SysException e) {
        log.error(e.getMessage(), e);
        saveExp(e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AuthorizationException.class)
    public Result handleAuthorizationException(AuthorizationException e) {
        log.error(e.getMessage(), e);
        saveExp(e);
        return Result.error("您没有权限，请联系管理员!");
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error(e.getMessage(), e);
        saveExp(e);
        return Result.error(e.getMessage());
    }

    /**
     * 文件上传最大限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handlerMaxUploadSizeExceededException(){
        return Result.error("您上传的文件超过最大限制20M!");
    }

    /**
     * 保存异常
     */
    private void saveExp(Exception e) {
        SysExpInfo exp = new SysExpInfo();
        exp.setExpection(ExceptionUtils.getFullStackTrace(e));
        exp.setUsername(ShiroUtils.getUserInfo().getUsername());
        exp.setCreateTime(new Date());
        sysExpService.save(exp);
    }

}
