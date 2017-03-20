package com.jueda.ndian.activity.circle.view;

import com.jueda.ndian.activity.circle.CircleContent;
import com.jueda.ndian.activity.circle.CircleContentObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 * 修改圈子信息
 */
public class CircleContentSetout implements CircleContentObservable {
    private List<CircleContent> pointList = new ArrayList<CircleContent>();
    @Override
    public void add(CircleContent point) {
        pointList.add(point);

    }

    @Override
    public void delect(CircleContent point) {
        int index = pointList.indexOf(point);
        if (index >= 0)
        {
            pointList.remove(index);
        }
    }

    @Override
    public void content(String content) {
        for (CircleContent observer : pointList)
        {
            observer.content(content);

        }
    }

    @Override
    public void name(String name) {
        for (CircleContent observer : pointList)
        {
            observer.name(name);
        }
    }

    @Override
    public void head(String head) {
        for (CircleContent observer : pointList)
        {
            observer.head(head);
        }
    }


}
