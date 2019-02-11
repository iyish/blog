package com.spring.sys.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.spring.common.annotation.SysLog;
import com.spring.common.shiro.ShiroUtils;
import com.spring.common.utils.Result;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 系统登录
 *
 * @author bigcatpan
 * @date 2018/3/5 14:37
 * @since V1.0
 */
@Controller
public class SysLoginController {
    @Autowired
    private Producer producer;

    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response) throws IOException {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到session
        ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 登录管理
     */
    @SysLog("登录系统")
    @PostMapping("/login")
    @ResponseBody
    public Result login(String username, String password, String captcha) {
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        if (!captcha.equalsIgnoreCase(kaptcha)) {
            return Result.error("验证码错误!");
        }
        try {
            Subject subject = ShiroUtils.getSubject();
            if (!subject.isAuthenticated()) {
                subject.login(new UsernamePasswordToken(username, password));
            }
        } catch (UnknownAccountException e) {
            return Result.error("账号不存在!");
        } catch (IncorrectCredentialsException e) {
            return Result.error("密码错误!");
        } catch (LockedAccountException e) {
            return Result.error("账号被锁定,请联系管理员!");
        } catch (AuthenticationException e) {
            return Result.error("账户验证失败!");
        }
        return Result.build();
    }

    /**
     * 退出系统管理
     */
    @SysLog("退出系统")
    @GetMapping("/logout")
    public String logout() {
        ShiroUtils.logout();
        return "redirect:login";
    }
}
