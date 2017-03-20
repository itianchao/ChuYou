package com.jueda.ndian.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * 修改指定位置的颜色
 * Created by Administrator on 2016/4/20.
 */
public class ChangeText {
    public ChangeText(String content, int color, TextView textView, int start, int end) {
        SpannableStringBuilder builder=new SpannableStringBuilder(content);
        ForegroundColorSpan teachingAdvantageColor=new ForegroundColorSpan(color);
        builder.setSpan(teachingAdvantageColor, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }
}
