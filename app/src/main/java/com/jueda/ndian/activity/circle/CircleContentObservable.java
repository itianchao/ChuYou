package com.jueda.ndian.activity.circle;

import com.jueda.ndian.activity.circle.CircleContent;

/**
 * Created by Administrator on 2016/8/15.
 * 修改圈信息
 */
public interface CircleContentObservable {
    public void add(CircleContent point);
    public void delect(CircleContent point);
    public void content(String content);
    public void name(String name);
    public void head(String head);
}
