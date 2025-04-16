package com.example.reggie.utils;

import java.io.File;

/**
 * @author 唐三
 * description: 图片判断工具类
 */
public class PhotoFiles {

    private static final String PHOTO_PATH = "D:\\javaProject\\SpringBoot项目\\lucky-app\\src\\main\\resources\\backend\\images\\menus";

    /**
     * 图片存在判断
     * @param httpPhotoName
     * @return
     */
    public static boolean isPhotoEmpty(String httpPhotoName) {
        /**
         * 思路:
         *     1,获取当前图片的文件夹,遍历所有图片
         *     2,与请求过来的图片进行判断,是否存在
         */
        System.out.println("运行了");
        //获取图片文件夹
        File photoFile = new File(PHOTO_PATH, httpPhotoName);
        //路径不存在
        if (!photoFile.exists()) {
            return  true;
        }
        return false;
    }
}
