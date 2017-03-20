package com.jueda.ndian.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/9/5.
 * 相机拍照位置
 */
public class ImageAddress {
    /**
     * 获取原图片存储路径
     * @return
     */
    public  static String ImageAddress(String name) {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = Environment.getExternalStorageDirectory()+"/ndian/ndianImages/";
        File file1 = new File(pathUrl);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String imageName = name+"imageTest.jpg";
        File file = new File(pathUrl);
        file.mkdirs();// 创建文件夹
        fileName = pathUrl + imageName;
        return fileName;
    }
}
