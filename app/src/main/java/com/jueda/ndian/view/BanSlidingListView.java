package com.jueda.ndian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 显示全部内容，禁止滑动
 * Created by Administrator on 2016/4/9.
 */
public class BanSlidingListView extends ListView{
    public BanSlidingListView(Context context) {
        super(context);
    }

    public BanSlidingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BanSlidingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
