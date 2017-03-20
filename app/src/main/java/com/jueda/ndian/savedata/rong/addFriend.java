package com.jueda.ndian.savedata.rong;

import android.content.Context;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import sortListView.SortModel;

/**
 * 添加用户信息
 * Created by Administrator on 2016/5/10.
 */
public class addFriend {
    //融云数据
    private List<Friend> userIdList;
    private DBManager manager;
    public addFriend(Context context, final List<SortModel> sourceDateList){
        //加载好友数据
        userIdList = new ArrayList<Friend>();
        for(int i=0;i<sourceDateList.size();i++){
            userIdList.add(new Friend(sourceDateList.get(i).getUid(), sourceDateList.get(i).getName(), sourceDateList.get(i).getAvatar()));
            /**第二次后就需要刷新用户信息*/
            RongIM.getInstance().refreshUserInfoCache(new UserInfo(sourceDateList.get(i).getUid(), sourceDateList.get(i).getName(), Uri.parse(sourceDateList.get(i).getAvatar())));
        }
        manager=new DBManager(context);
        manager.add(userIdList);


        /**第一次写入用户信息*/
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String userId) {
                for (Friend i : userIdList) {
                    if (i.getUserId().equals(userId)) {
                        return new UserInfo(i.getUserId(), i.getUserName(), Uri.parse(i.getPortraitUri()));//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
                    }
                }
                return null;
            }
        }, true);

    }
}
