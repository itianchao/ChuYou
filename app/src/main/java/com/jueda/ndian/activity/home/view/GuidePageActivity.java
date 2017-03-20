package com.jueda.ndian.activity.home.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.GuidePageFragment;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import java.util.ArrayList;
import java.util.List;

/***
 * 引导页
 */
public class GuidePageActivity extends FragmentActivity {
    private ViewPager vp;
    private List<Fragment> views;
    private ImageView[] dots ;
    private static final int[] pics={R.drawable.one,R.drawable.two,R.drawable.three};
        //记录当前选中位置
    private int currentIndex;  //记录当前选中位置
//    private LinearLayout ll ;//点的管理者
    private int lastState=0;//记录滑动状态；
    private int last=0;//记录滑动的位置
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_guide_page);
        InitView();
        setData();
    }
    private void setData() {
        GuidePageFragment f1=new GuidePageFragment();
        GuidePageFragment f2=new GuidePageFragment();
        GuidePageFragment f3=new GuidePageFragment();
        views.add(f1);
        views.add(f2);
        views.add(f3);
        Bundle b1=new Bundle();
        b1.putInt("image", pics[0]);
        b1.putBoolean("last", false);
        f1.setArguments(b1);

        Bundle b2=new Bundle();
        b2.putInt("image", pics[1]);
        b2.putBoolean("last", false);
        f2.setArguments(b2);

        Bundle b3=new Bundle();
        b3.putInt("image", pics[2]);
        b3.putBoolean("last", true);
        f3.setArguments(b3);


        FragmentManager manager=this.getSupportFragmentManager();
        GuidepageAdapter adapter=new GuidepageAdapter(manager,views);
        vp.setAdapter(adapter);
//        vp.setOnPageChangeListener(this);
//        initDots();
    }
//    //给点赋值
//    private void initDots() {
//        dots = new ImageView[pics.length];
//        for (int i = 0; i < pics.length; i++) {
//            dots[i] = (ImageView) ll.getChildAt(i);
//            dots[i].setEnabled(true);//都设为灰色
//            dots[i].setOnClickListener(this);
//            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
//        }
//        currentIndex = 0;
//        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
//    }
    private void InitView() {
        views=new ArrayList<>();
        vp = (ViewPager) findViewById(R.id.viewPager);
//        ll = (LinearLayout) findViewById(R.id.ll);
    }
//    /**
//     *设置当前的引导页
//     */
//    private void setCurView(int position)
//    {
//        if (position < 0 || position >= pics.length) {
//            return;
//        }
//
//        vp.setCurrentItem(position);
//    }
//    /**
//     *这只当前引导小点的选中
//     */
//    private void setCurDot(int positon)
//    {
//        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
//            return;
//        }
//
//        dots[positon].setEnabled(false);
//        dots[currentIndex].setEnabled(true);
//
//        currentIndex = positon;
//    }
//    @Override
//    public void onPageScrollStateChanged(int arg0) {
//        if(last==4){
//            if(arg0==1){
//                lastState=1;
//            }else if(arg0==2){
//                lastState=2;
//            }else if(arg0==0&&lastState==1){
//
//            }
//        }
//    }
//    @Override
//    public void onPageScrolled(int arg0, float arg1, int arg2) {
//        last=arg0;
//    }
//    @Override
//    public void onPageSelected(int arg0) {
//        setCurDot(arg0);
//    }
//    @Override
//    public void onClick(View v) {
//        int position = (Integer)v.getTag();
//        setCurView(position);
//        setCurDot(position);
//    }

    /**
     * 返回无效
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;

        }
        return super.onKeyUp(keyCode, event);
    }
}
