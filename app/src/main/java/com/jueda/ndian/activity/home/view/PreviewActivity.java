package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * 图片查看
 */
public class PreviewActivity  extends ActionBarActivity {
    public static final String POSITION="position";
    public static final String LIST="list";


    private int position;
    private ArrayList<Photo> list;//图片集合
    private ViewPager mViewPager;

    private ImageButton delectImageButton;//删除
    private int tag=0;//记录删除的位置
    private  SamplePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.i_pre_view);

        //获取信息
        tag=position=getIntent().getIntExtra(POSITION,0);
        list=getIntent().getParcelableArrayListExtra(LIST);



        Constants.IS_CANCEL=1;
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        delectImageButton=(ImageButton)findViewById(R.id.delectImageButton);



        NdianApplication.instance.setTitle(this, (position + 1) + "/" + list.size(), true);
        mAdapter = new SamplePagerAdapter(list);
        mViewPager.setAdapter(mAdapter);
        //默认选择第position张图片
        mViewPager.setCurrentItem(position,false);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tag = position;
                NdianApplication.instance.setTitle(PreviewActivity.this, position + 1 + "/" + list.size(), true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        delectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                if(list.size()>1){
                    if(tag==0){
                        list.remove(tag);
                        NdianApplication.instance.setTitle(PreviewActivity.this, 1 + "/" + list.size(), true);
                        mAdapter = new SamplePagerAdapter(list);
                        mViewPager.setAdapter(mAdapter);
                        mViewPager.setCurrentItem(i);
                    }else {
                        list.remove(tag);
                        NdianApplication.instance.setTitle(PreviewActivity.this, tag + "/" + list.size(), true);
                        i=tag-1;
                        mAdapter = new SamplePagerAdapter(list);
                        mViewPager.setAdapter(mAdapter);
                        mViewPager.setCurrentItem(i);
                    }
                }else{
                    list.clear();
                    finish();
                }


            }
        });

    }
    @Override
    public void finish()
    {
        Intent intent = new Intent();
        intent.putExtra(Constants.RES_PHOTO_LIST, list);
        setResult(RESULT_OK, intent);
        super.finish();
    }



    class SamplePagerAdapter extends PagerAdapter
    {
        private List<Photo> mList;

        public SamplePagerAdapter(List<Photo> list)
        {
            this.mList = list;
        }

        @Override
        public int getCount()
        {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position)
        {
            final PhotoView photoView = new PhotoView(container.getContext());

                ImageLoaderUtil.ImageLoader("file:///" + mList.get(position).path, photoView);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }
    }

}
