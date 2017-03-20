package com.jueda.ndian.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jueda.ndian.R;

/**
 * 自定义View：开关按钮
 * 自定义view的流程：1.继承view，2重写onDraw，进行绘制，3.重写onMeasure修改尺寸，4. 在xml中修改配置
 * @author Administrator
 *
 */
public class SwitchButton extends View {

    private String ATTR_IS_OPENED = "isOpened";

    public interface OnOpenedListener {

        void onChecked(View v, boolean isOpened);
    }

    /*
     * 画笔是复杂的对象，所以需要重用。 必须创建在成员区域并在初始化时赋值
     */
    Paint mPaint;
    Bitmap mSwitchBackground;
    Bitmap mSlideButton;
    int mMaxLeft;
    int mCurrLeft;
    boolean isOpen = true;

    private int  switchBackgroundResId_true;
    private int  switchBackgroundResId_false;

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwitchButton(Context context) {
        super(context);
        init(null);
    }

    /* 初始化各种组件 */
    private void init(AttributeSet attrs) {
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);

        // 初始化背景图片
//		mSwitchBackground = BitmapFactory.decodeResource(getResources(),
//				R.drawable.switch_background);
//		mSlideButton = BitmapFactory.decodeResource(getResources(),
//				R.drawable.slide_button);

        if (attrs != null){
            isOpen = attrs.getAttributeBooleanValue(R.styleable.SwitchButton_isOpened, true);
            int  slideButtonResId= attrs.getAttributeResourceValue(R.styleable.SwitchButton_slide_button,R.drawable.botiao);
            mSlideButton = BitmapFactory.decodeResource(getResources(), slideButtonResId);
            switchBackgroundResId_true= attrs.getAttributeResourceValue(R.styleable.SwitchButton_switch_background_true, R.drawable.on_on);
            switchBackgroundResId_false= attrs.getAttributeResourceValue(R.styleable.SwitchButton_switch_background_false,R.drawable.on_off);
            if(isOpen){
                mSwitchBackground = BitmapFactory.decodeResource(getResources(), switchBackgroundResId_true);
            }else{
                mSwitchBackground = BitmapFactory.decodeResource(getResources(), switchBackgroundResId_false);
            }




            if (mSlideButton == null || mSwitchBackground == null){
                throw new NullPointerException("资源图片不能为空");
            }
        }

        // 计算最大可滑动距离
        mMaxLeft = mSwitchBackground.getWidth() - mSlideButton.getWidth();

        // 设置开关事件
        // 已将点击事件的逻辑，防到 onTouchEvent 的 ACTION_UP 中

        // 初始化开关状态
        initStatus(attrs);
    }

    /* 初始化开关状态 */
    private void initStatus(AttributeSet attrs) {
        if (attrs != null){
            setStatus(isOpen);
        }
    }
    /**设置初始化状态*/
    public void setIsOpen(boolean isOpen){
        this.isOpen=isOpen;
        setStatus(isOpen);
    }

    /* 设置状态 */
    private void setStatus(boolean status){
        if (status){
            mCurrLeft = mMaxLeft;
            isOpen = true;
            mSwitchBackground = BitmapFactory.decodeResource(getResources(), switchBackgroundResId_true);
        } else {
            mCurrLeft = 0;
            isOpen = false;
            mSwitchBackground = BitmapFactory.decodeResource(getResources(), switchBackgroundResId_false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSwitchBackground.getWidth(), mSwitchBackground.getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mSwitchBackground, 0, 0, mPaint);
        canvas.drawBitmap(mSlideButton, mCurrLeft, 0, mPaint);
    }

    private OnOpenedListener mOpenedkListener;
    public void setOnCheckChangedListener(OnOpenedListener checkedkListener) {
        this.mOpenedkListener = checkedkListener;
    }

    int startX;
    int moveX;

    /**
     * event.getX() 基于控件本身
     * event.getRawX() 基于整个屏幕
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int distance = (int) (event.getX() - startX);
                mCurrLeft += distance;
                startX = (int) event.getX();
			
			/*
			 * 处理本次触摸总移动的距离，必须用绝对值，避免往回滑，moveX被减小
			 */
                moveX += Math.abs(distance);
                break;
            case MotionEvent.ACTION_UP:
                if (moveX < 5){
                    // 用户的本意是点击
                    if (isOpen){
                        setStatus(!isOpen);
                    } else {
                        setStatus(!isOpen);
                    }
                    //处理回调
                    if (mOpenedkListener != null){
                        mOpenedkListener.onChecked(this, isOpen);
                    }

                } else {
                    // 用户的本意是滑动
                    setStatus(mCurrLeft >= mMaxLeft / 2);

                    //处理回调
                    if (mOpenedkListener != null){
                        mOpenedkListener.onChecked(this, isOpen);
                    }
                }

                moveX = 0;
                break;
        }

        // 边界处理
        if (mCurrLeft < 0){
            mCurrLeft = 0;
        }

        if (mCurrLeft > mMaxLeft){
            mCurrLeft = mMaxLeft;
        }

        // 引起重绘
        invalidate();
		
		/*
		 * 如果设置了点击事件：
		 *  返回true：能监听 ACTION_MOVE，但不能响应点击事件
		 *  返回true：不能监听 ACTION_MOVE，也不能响应点击事件
		 *  返回super.onTouchEvent(event)：能监听 ACTION_MOVE，也可以监听 点击事件（但是点击事件在onTouchEvent之后）
		 *  
		 *   如果没有设置点击事件：
		 *  返回true：能监听 ACTION_MOVE
		 *  返回true：不能监听 ACTION_MOVE
		 *  返回super.onTouchEvent(event)：不能监听 ACTION_MOVE
		 */
        return true;
    }

}
