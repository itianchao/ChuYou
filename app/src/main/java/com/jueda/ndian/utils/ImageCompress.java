package com.jueda.ndian.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageCompress {
	public static File ImageCompress(String path,String name){
        File file = null;
        try {
            // 按比例压缩图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int reqHeight = 480;
            int reqWidth = 800;
            int zoomRatio = 1;
            if (width > height && width > reqWidth) {
                zoomRatio =  (width / reqWidth);
            } else if (width < height && height > reqHeight) {
                zoomRatio =  (height / reqHeight);
            }
//            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inSampleSize=zoomRatio;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            //比例压缩后质量压缩
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options1=100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            while(baos.toByteArray().length/1024>1024){
                if(options1<=0){
                    break;
                }
                baos.reset();
                options1-=10;

                bitmap.compress(Bitmap.CompressFormat.JPEG, options1, baos);
                int l=baos.toByteArray().length;
            }
            //讲字节输出流转换成file，转换成file

            File f=new File(Environment.getExternalStorageDirectory()+"/ndian/ndianImages/");
            if(!f.exists()){
                f.mkdirs();
            }
            file=new File(Environment.getExternalStorageDirectory()+"/ndian/ndianImages/"+name+".png");
            try {
                FileOutputStream fos=new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                bitmap.recycle();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }catch (OutOfMemoryError e){
            e.printStackTrace();
            return null;
        }
        return file;
     }
	private static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
            }
		}
		return inSampleSize;
	}
}
