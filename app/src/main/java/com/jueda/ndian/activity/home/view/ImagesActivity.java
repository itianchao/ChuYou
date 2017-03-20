package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.ImagesAdapter;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import java.util.ArrayList;

/***
 * 图片选择
 */
public class ImagesActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String ARG_DIR_ID = "my.android.app.chooseimages.DIR_ID";
    public static final String ARG_DIR_NAME = "my.android.app.chooseimages.DIR_NAME";
    private GridView mGridView;

    private ImagesAdapter mAdapter;

    private String mDirId;
    private TextView number;//记录个数
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.i_images_griview);
        //记录个数
        number=(TextView)findViewById(R.id.number);
        if(Constants.checkList.size()<=0){
            number.setVisibility(View.GONE);
        }else{
            number.setVisibility(View.VISIBLE);
            number.setText(Constants.checkList.size()+"");
        }
        /**取消*/
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.IS_CANCEL=2;
                finish();
                ImageDirActivity.instance.finish();

            }
        });
        /**
         * 完成
         */
        findViewById(R.id.achieveRelativeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.IS_CANCEL=1;
                finish();
                ImageDirActivity.instance.finish();
            }
        });
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = (Photo) parent.getItemAtPosition(position);
                if (photo == null) {
                    return;
                }

                if (!Constants.checkList.contains(photo)) {
                    if (Constants.checkList.size() >= Constants.MAX_SIZE && Constants.MAX_SIZE >= 0) {
                        Toast.makeText(ImagesActivity.this,
                                "最多只能选择"+Constants.MAX_SIZE+"张",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                }
                mAdapter.setCheck(position, view);
                if(Constants.checkList.size()<=0){
                    number.setVisibility(View.GONE);
                }else{
                    number.setVisibility(View.VISIBLE);
                    number.setText(Constants.checkList.size()+"");
                }
            }
        });
        mGridView.setOnScrollListener(ImageLoaderUtil.pauseScrollListener);

        Intent intent = getIntent();
        mDirId = intent.getStringExtra(ARG_DIR_ID);
        setTitle(intent.getStringExtra(ARG_DIR_NAME));
        NdianApplication.instance.setTitle(this, intent.getStringExtra(ARG_DIR_NAME), true);
        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.RESULT_CHANGE)
        {
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Media.DATA//图片地址
                },
                mDirId == null ? null : MediaStore.Images.Media.BUCKET_ID + "=" + mDirId,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            ArrayList<Photo> list = new ArrayList<Photo>();

            cursor.moveToPosition(-1);
            while (cursor.moveToNext())
            {
                Photo photo = new Photo();

                photo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                list.add(photo);
            }

            mAdapter = new ImagesAdapter(this, list, Constants.checkList);
            mGridView.setAdapter(mAdapter);
        }
        else
        {
            Toast.makeText(this, "本目录下没有图片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
    }
}
