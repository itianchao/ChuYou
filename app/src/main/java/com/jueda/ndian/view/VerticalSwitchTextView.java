package com.jueda.ndian.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jueda.ndian.R;
import java.util.List;

/**
 * Created by lixueyong on 16/2/29.
 */
public class VerticalSwitchTextView extends TextView implements View.OnClickListener {
    private static final int DEFAULT_SWITCH_DURATION = 500;
    private static final int DEFAULT_IDLE_DURATION = 2000;
    private Context mContext;

    private List<String> lists;//会循环显示的文本内容
    private int contentSize;
    private String outStr;//当前滑出的文本内容
    private String inStr;//当前滑入的文本内容
    private float textBaseY;//文本显示的baseline
    private int currentIndex = 0;//当前显示到第几个文本

    private int switchDuaration = DEFAULT_SWITCH_DURATION;//切换时间
    private int idleDuaration = DEFAULT_IDLE_DURATION;//间隔时间
    private int switchOrientation = 0;

    private float currentAnimatedValue = 0.0f;
    private ValueAnimator animator;

    private int verticalOffset = 0;
    private int mWidth;
    private int mHeight;
    private int paddingLeft = 0;
    private int paddingBottom = 0;
    private int paddingTop = 0;

    private Paint mPaint;

    //回调接口，用来通知调用者控件当前的状态
    public VerticalSwitchTextViewCbInterface cbInterface;

    public VerticalSwitchTextView(Context context) {
        this(context, null);
    }

    public VerticalSwitchTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSwitchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalSwitchTextView);
        try {
            switchDuaration = array.getInt(R.styleable.VerticalSwitchTextView_switchDuaration, DEFAULT_SWITCH_DURATION);
            idleDuaration = array.getInt(R.styleable.VerticalSwitchTextView_idleDuaration, DEFAULT_IDLE_DURATION);
            switchOrientation = array.getInt(R.styleable.VerticalSwitchTextView_switchOrientation, 0);
        } finally {
            array.recycle();
        }
        init();
    }

    private void init() {
        setOnClickListener(this);

        mPaint = getPaint();
        mPaint.setColor(getCurrentTextColor());

        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(switchDuaration);
        animator.setStartDelay(idleDuaration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnimatedValue = (float) animation.getAnimatedValue();
                if (currentAnimatedValue < 1.0f) {
                    invalidate();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentIndex = (++currentIndex) % contentSize;
                if (cbInterface != null) {
                    cbInterface.showNext(currentIndex);
                }
                outStr = lists.get(currentIndex);
                inStr = lists.get((currentIndex + 1) % contentSize);

                animator.setStartDelay(idleDuaration);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 设置循环显示的文本内容
     *
     * @param content 内容list
     */
    public void setTextContent(List<String> content) {
        lists = content;
        if (lists == null || lists.size() == 0) {
            return;
        }
        contentSize = lists.size();

        outStr = lists.get(0);
        if (contentSize > 1) {
            inStr = lists.get(1);
        } else {
            inStr = lists.get(0);
        }

        if (contentSize > 0) {
            animator.start();
        }
    }

    /**
     * 主要用来调整TextView的高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);

        Rect bounds = new Rect();
        if (contentSize <= 0) {
            return;
        }
        String text = lists.get(0);
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        int textHeight = bounds.height();

        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
        mHeight = textHeight + paddingBottom + paddingTop;

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        //计算文字的baseline
        textBaseY = mHeight - (mHeight - fontHeight) / 2 - fontMetrics.bottom;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (contentSize <= 0) {
            return;
        }

        //直接使用mHeight控制文本绘制，会因为text的baseline的问题不能居中显示
        verticalOffset = Math.round(2 * textBaseY * (0.5f - currentAnimatedValue));
        if (switchOrientation == 0) {//向上滚动切换
            if (verticalOffset > 0) {
                canvas.drawText(outStr, paddingLeft, verticalOffset, mPaint);
            } else {
                canvas.drawText(inStr, paddingLeft, 2 * textBaseY + verticalOffset, mPaint);
            }
        } else {
            if (verticalOffset > 0) {//向下滚动切换
                canvas.drawText(outStr, paddingLeft,  2 * textBaseY - verticalOffset, mPaint);
            } else {
                canvas.drawText(inStr, paddingLeft, -verticalOffset, mPaint);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (contentSize > currentIndex) {
            if (cbInterface != null) {
                cbInterface.onItemClick(currentIndex);
            }
        }
    }

    //回调接口，用来通知调用者控件当前的状态,index表示开始显示哪一个文本内容
    public interface VerticalSwitchTextViewCbInterface {
        void showNext(int index);
        void onItemClick(int index);
    }

    public void setCbInterface(VerticalSwitchTextViewCbInterface cb) {
        cbInterface = cb;
    }
}
