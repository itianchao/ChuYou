package com.jueda.ndian.activity.fragment;

import android.support.v4.app.Fragment;

/**
 * 懒加载
 * Created by Administrator on 2016/7/14.
 */
public abstract class LazyFragment extends Fragment {
    protected boolean isVisible=false;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()&&isVisible==false) {
            isVisible = true;
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible(){}
}
