package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.utils.PhotoFiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author  唐三
 * description: 文件上传与下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //获取配置文件信息
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 图片下载
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        /**
         * 图片下载思路:
         *      通过HttpServletResponse修改他的编码类型
         *     先获取当前文件路径
         *     使用流进行图片响应
         */
        log.info("dish download");
        response.setContentType("*/*;charset=utf-8");

        if (PhotoFiles.isPhotoEmpty(name)) {
            log.info("图片不存在");
            return;
        }
        try(
                //输入流
                InputStream input = new FileInputStream(basePath + name);
                BufferedInputStream bis = new BufferedInputStream(input);
                //输出流
                OutputStream out = response.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(out)
        ) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer,0,len);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        //file是一个临时文件,需要转存到指定位置,本次请求结束,临时文件删除
        //C:\Users\lihua\AppData\Local\Temp\tomcat.80.16201086992663455879\work\Tomcat
        //临时文件最终在这个目录下.tmp结尾,我们可以自行改为.jpg
        /**
         * 图片上传思路:
         *     1,我们先获取图片名称
         *           图片可能为空
         *     2,如果图片名称重复,对其进行处理
         *     3,片段上传路径是否存在
         *     4,图片下载
         */

        log.info("upload : {}", file.getOriginalFilename());
        //获取图片全名称
        String photo = file.getOriginalFilename();
        //当用户问输入任何图片时
        if (StringUtils.isEmpty(photo)) {
            return R.error("图片不存在");
        }
        //uuid图片去重
        photo = UUID.randomUUID().toString() + photo.substring(photo.lastIndexOf("."));
        //判断路径是否存在
        File realPath = new File(basePath);
        //路径不存在创建
        if (!realPath.exists()) {
            realPath.mkdirs();
        }
        System.out.println("图片名称:" + photo);
        try {
            file.transferTo(new File(basePath + photo));
            System.out.println("执行了");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(photo);
    }

    @GetMapping("/aa")
    public R<String> aa(String name) {
        return R.success(name);
    }
}
