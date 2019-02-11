package com.spring.profile.controller;

import com.spring.common.mvc.BaseController;
import com.spring.common.utils.Constants;
import com.spring.common.utils.FileUtils;
import com.spring.common.utils.JsonUtils;
import com.spring.profile.pojo.SysHeadInfo;
import com.spring.profile.service.SysHeadService;
import com.spring.sys.service.SysUserService;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Title: SysHeadController
 * @Description: 系统头像
 * @author bigcatpan
 * @date 2018/9/2 9:19
 * @since v1.0
 */
@Controller
@RequestMapping("/sys/head")
public class SysHeadController extends BaseController {
    /**
     * 返回结果转为JSON给前端
     */
    private class Result {
        /**
         * 表示图片是否已上传成功。
         */
        public Boolean success;
        /**
         * 自定义的附加消息。
         */
        public String msg;
        /**
         * 表示原始图片的保存地址。
         */
        public String srcImgName;
        /**
         * 表示头像图片的保存地址
         */
        public String headImgName;
    }

    @Autowired
    SysHeadService sysHeadService;
    @Autowired
    SysUserService sysUserService;

    /**
     * 查询头像
     * @param request 请求
     * @param response 响应
     */
    @GetMapping("/img")
    public void img(HttpServletRequest request, HttpServletResponse response) {
        String headSaveDir = sysUserService.getParamValueByKey("head.default.dir", Constants.HEAD_SAVE_DIR);
        String uploadPath = request.getServletContext().getRealPath("/") + File.separator + headSaveDir;
        SysHeadInfo sysHeadInfo = sysHeadService.queryHeadInfoById(getUserId());
        String imgName = null;
        if (sysHeadInfo != null) {
            imgName = sysHeadService.queryHeadInfoById(getUserId()).getHeadImgName();
        }
        File file = new File(uploadPath + File.separator + imgName);
        if (!(file.exists() && file.canRead())) {
            file = new File(request.getServletContext().getRealPath("/") + File.separator + Constants.DEFAULT_HEAD_PATH);
            log.info("head is not found, default head is used");
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            response.setContentType("image/jpeg");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            Streams.copy(inputStream, outputStream, true);
        } catch (FileNotFoundException fnfe) {
            log.error("head file is not found, check filepath:{}, exception:\n{}",
                    file.getPath(), ExceptionUtils.getStackTrace(fnfe));
        } catch (IOException ioe) {
            log.error("get head I/O error occurred,exception:\n{}", ExceptionUtils.getFullStackTrace(ioe));
        }
    }

    /**
     * 上传头像
     * @param request request
     * @param srcHead 头像原图片上传文件
     * @param head 生成的头像上传文件
     * @param response response
     */
    @PostMapping("/upload")
    public void upload(HttpServletRequest request,
                       @RequestParam(value = "__source", required = false) MultipartFile srcHead,
                       @RequestParam("__avatar1") MultipartFile head,
                       HttpServletResponse response) {
        String headSaveDir = sysUserService.getParamValueByKey("head.default.dir",Constants.HEAD_SAVE_DIR);
        String uploadPath = request.getServletContext().getRealPath("/") + File.separator + headSaveDir;
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String srcHeadFileName = "";
        String srcHeadPath;
        String headFileName;
        String headPath;
        // 如果头像存在则先删除再上传
        SysHeadInfo hInfo = sysHeadService.queryHeadInfoById(getUserId());
        if (hInfo != null) {
            File headFile = new File(uploadPath + File.separator + hInfo.getHeadImgName());
            if (headFile.exists()) {
                headFile.delete();
            }
            if (hInfo.getSrcImgName() != null && !hInfo.getSrcImgName().isEmpty()) {
                File srcHeadFile = new File(uploadPath + File.separator + hInfo.getSrcImgName());
                if (srcHeadFile.exists()) {
                    srcHeadFile.delete();
                }
            }
        }
        headFileName = "head_" + FileUtils.genFileSuffix(8) + ".jpg";
        headPath = uploadPath + File.separator + headFileName;
        try {
            if (srcHead != null && !srcHead.isEmpty()) {
                srcHeadFileName = "src_" + FileUtils.genFileSuffix(8) + ".jpg";
                srcHeadPath = uploadPath + File.separator + srcHeadFileName;
                srcHead.transferTo(new File(srcHeadPath));
            }
            head.transferTo(new File(headPath));
            SysHeadInfo headInfo = new SysHeadInfo();
            headInfo.setUserId(getUserId());
            headInfo.setSrcImgName(srcHeadFileName);
            headInfo.setHeadImgName(headFileName);
            sysHeadService.save(headInfo);
            // 返回json结果
            Result result = new Result();
            result.success = true;
            result.msg = "success";
            result.srcImgName = srcHeadFileName;
            result.headImgName = headFileName;
            JsonUtils.outputJson(result,response);
        } catch (IOException ioe) {
            log.error("upload file failed,exception:{}", ExceptionUtils.getStackTrace(ioe));
        }
    }
}
