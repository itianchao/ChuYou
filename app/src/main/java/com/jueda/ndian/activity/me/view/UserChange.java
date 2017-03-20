package com.jueda.ndian.activity.me.view;

import com.jueda.ndian.activity.me.UserContent;
import com.jueda.ndian.activity.me.UserObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class UserChange implements UserObservable {
    private List<UserContent> pointList = new ArrayList<UserContent>();
    @Override
    public void add(UserContent point) {
        pointList.add(point);
    }

    @Override
    public void delect(UserContent point) {
        int index = pointList.indexOf(point);
        if (index >= 0)
        {
            pointList.remove(index);
        }
    }

    @Override
    public void bean() {
        for (UserContent observer : pointList)
        {
            observer.bean();

        }
    }

    @Override
    public void money() {
        for (UserContent observer : pointList)
        {
            observer.money();

        }
    }
}
