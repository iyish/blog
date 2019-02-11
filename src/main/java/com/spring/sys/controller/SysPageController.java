package com.spring.sys.controller;

import com.spring.common.mvc.BaseController;
import com.spring.common.utils.Constants;
import com.spring.sys.pojo.SysResInfo;
import com.spring.sys.service.SysParamService;
import com.spring.sys.service.SysResService;
import com.spring.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * 系统跳转页面
 *
 * @author bigcatpan
 * @date 2018/3/6 17:21
 * @since V1.0
 */
@Controller
public class SysPageController extends BaseController {
    @Autowired
    SysResService sysResService;
    @Autowired
    SysUserService sysUserService;

    @GetMapping("admin.html")
    public String admin(Model model) {
        List<SysResInfo> navs = sysResService.getNav(getUserId());
        model.addAttribute("navs", navs);
        String skin = sysUserService.getParamValueByKey("sys.default.skin", Constants.SkinTheme.DEFAULT.getValue());
        model.addAttribute("defaultSkin", skin);
        model.addAttribute("username", getUser().getUsername());
        return "admin";
    }

    @GetMapping("{module}/{url}.html")
    public String module(@PathVariable("module") String module, @PathVariable("url") String url) {
        return module + "/" + url;
    }

    @GetMapping("{url}.html")
    public String url(@PathVariable("url") String url) {
        return url;
    }

}
