package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jueda.ndian.R;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.SystemBarTintManager;
import com.jueda.ndian.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PreviewActivity extends AppCompatActivity {
    public static final String POSITION="position";
    public static final String LIST="list";
    private ArrayList<Photo> list;//图片集合
    private HackyViewPager mViewPager;
    private int p;
    private  SamplePagerAdapter mAdapter;
    private LinearLayout linearLayout;//指示点
    private RelativeLayout relativeLayout;//触摸关闭
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangeTitle(this);
        setContentView(R.layout.activity_preview);
        //获取信息
        p=getIntent().getIntExtra(POSITION, 0);
        list=getIntent().getParcelableArrayListExtra(LIST);


        mViewPager = (HackyViewPager) findViewById(R.id.viewpager);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        mAdapter = new SamplePagerAdapter(list);
        mViewPager.setAdapter(mAdapter);
        /**指示器*/
        point();

        //默认选择第position张图片
        mViewPager.setCurrentItem(p, false);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView imageView= (ImageView) linearLayout.getChildAt(position);
                imageView.setImageResource(R.drawable.kuang_corners_red_dian_five);

                ImageView imageView1= (ImageView) linearLayout.getChildAt(p);
                imageView1.setImageResource(R.drawable.kuang_corners_white_five);
                p=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //点击关闭
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                ImageLoaderUtil.ImageLoader(mList.get(position).path, photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    PreviewActivity.this.finish();
                }
            });
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

    private void point(){
        for(int i=0;i<list.size();i++){
            LinearLayout.LayoutParams imgvwDimens =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ImageView im = new ImageView(PreviewActivity.this);
            //图片充满界面
            im.setLayoutParams(imgvwDimens);
            im.setPadding(0, 0, Constants.dip2px(PreviewActivity.this, 10), 0);
            if(i==p){
                im.setImageResource(R.drawable.kuang_corners_red_dian_five);
            }else{
                im.setImageResource(R.drawable.kuang_corners_white_five);
            }
            linearLayout.addView(im);
        }

    }

    /**
     * 改变导航栏背景
     * @param context
     */
    public void ChangeTitle(Context context){
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager((Activity) context);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 设置一个样式背景给导航栏
        tintManager.setNavigationBarTintResource(R.color.black);
        tintManager.setStatusBarTintResource(R.color.black);
    }
}
