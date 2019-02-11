package com.spring.common.mvc;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 自定义FreeMarkerView,实现绝对路径
 *
 * @author bigcatpan
 * @date 2018/3/6 18:07
 * @since V1.0
 */
public class MyFreeMarkerView extends FreeMarkerView {
    private static final String CONTEXT_PATH = "base";

    @Override
    protected void exposeHelpers(Map<String, Object> model,
                                 HttpServletRequest request) throws Exception {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String path = request.getContextPath();
        String basePath = scheme + "://" + serverName + ":" + port + path + "/";
        model.put(CONTEXT_PATH, basePath);
        super.exposeHelpers(model, request);
    }
}
