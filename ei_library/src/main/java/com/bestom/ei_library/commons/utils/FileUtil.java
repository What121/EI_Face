package com.bestom.ei_library.commons.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return void
     */
    public static String SavaImage(Bitmap bitmap, String path){
        String picturePath=path+"registimg.jpg";
        File file=new File(path);
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        try {
            fileOutputStream=new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
            fileOutputStream.close();
            return picturePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "图片写入本地失败";
        }
    }


    public static Bitmap getBitmapForImgPath(String imgpath) {
        Bitmap btp=null;
        InputStream is = null;
        try {
            is = new FileInputStream(imgpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 1;
        btp = BitmapFactory.decodeStream(is, null, options);
//    btp.recycle();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return btp;
    }



}
