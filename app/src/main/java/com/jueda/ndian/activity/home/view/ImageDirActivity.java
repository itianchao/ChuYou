package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.ImageDirAdapter;
import com.jueda.ndian.entity.Dir;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import java.util.ArrayList;

/**
 * 图片选择目录
 */
public class ImageDirActivity  extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mListView;
    public static ImageDirActivity instance=null;
    private TextView number;//记录个数

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        instance=this;
        setContentView(R.layout.i_image_dir);

        /**默认是取消*/
        Constants.IS_CANCEL=2;
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.directory), true);

        mListView = (ListView) findViewById(R.id.listview);
        /**取消*/
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.IS_CANCEL=2;
                finish();
            }
        });
        /** 完成*/
        findViewById(R.id.achieveRelativeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.IS_CANCEL = 1;
                finish();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dir dir = (Dir) parent.getItemAtPosition(position);
                if (dir != null) {
                    Intent intent = new Intent(ImageDirActivity.this, ImagesActivity.class);
                    intent.putExtra(ImagesActivity.ARG_DIR_ID, dir.id);
                    intent.putExtra(ImagesActivity.ARG_DIR_NAME, dir.name);
                    intent.putExtra(Constants.ARG_PHOTO_LIST, Constants.checkList);
                    startActivityForResult(intent, RESULT_OK);
                }
            }
        });

//        if (savedInstanceState == null)
//        {
//            ArrayList<Photo> list = getIntent().getParcelableArrayListExtra(Constan.ARG_PHOTO_LIST);
//            if (list != null)
//            {
//                Constants.checkList.addAll(list);
//            }
//        }
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            finish();
        }
    }
    @Override
    protected void onResume() {
        //记录个数
        number=(TextView)findViewById(R.id.number);
        if(Constants.checkList.size()<=0){
            number.setVisibility(View.GONE);
        }else{
            number.setText(Constants.checkList.size()+"");
            number.setVisibility(View.VISIBLE);
        }
        super.onPause();
    }


    @Override
    public void finish()
    {
        Intent intent = new Intent();
        intent.putExtra(Constants.RES_PHOTO_LIST, Constants.checkList);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        "count(1) length",
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATA
                },
                "1=1) GROUP BY " + MediaStore.Images.Media.BUCKET_ID + " -- (",
                null,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC," +
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            ArrayList<Dir> list = new ArrayList<Dir>();

            cursor.moveToPosition(-1);
            while (cursor.moveToNext())
            {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String dirPath;
                int index = path.lastIndexOf('/');
                if (index > 0)
                {
                    dirPath = path.substring(0, index);
                }
                else
                {
                    dirPath = path;
                }
                Dir dir = new Dir();
                dir.id = String.valueOf(id);
                dir.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                dir.text = dirPath;
                dir.path = path;
                dir.length = cursor.getInt(cursor.getColumnIndex("length"));
                list.add(dir);
            }

            ImageDirAdapter adapter = new ImageDirAdapter(this, list);
            mListView.setAdapter(adapter);
        }
        else
        {
            Toast.makeText(this, "没有存在图片的目录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
    }
}
