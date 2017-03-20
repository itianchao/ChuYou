package com.jueda.ndian.activity.home.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jueda.ndian.R;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.ClipSquareImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**图片裁剪*/
public class CutImageViewActivity extends AppCompatActivity {
    private ClipSquareImageView imageView;
    public static  Bitmap myBitmap;
    public static Bitmap cutBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_image_view);

        imageView = (ClipSquareImageView) findViewById(R.id.clipSquareIV);
        if(myBitmap!=null){
            imageView.setImageBitmap(myBitmap);
        }
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.doneBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 此处获取剪裁后的bitmap
                cutBitmap = imageView.clip();
                if(cutBitmap!=null){
                    setResult(Constants.CUT_IMAGE);
                }else{
                    new ToastShow(CutImageViewActivity.this,"图片有误",1000);
                }
                finish();
            }
        });
    }
    /**uri转bitmap*/
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream;
        outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

}
