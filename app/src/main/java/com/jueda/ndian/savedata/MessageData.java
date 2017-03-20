package com.jueda.ndian.savedata;

import android.content.Context;
import android.content.SharedPreferences;

import com.jueda.ndian.entity.MessageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/27.
 * 保存用户消息信息
 */
public class MessageData {
    private static JSONArray jsonArray;
    private static JSONObject object;
    private static JSONObject object2;
    private static ArrayList<MessageEntity> List;
    //保存用户集合
    public static void writeaMessageData(Context context, java.util.List<MessageEntity> list) {
        if (null == context ) {
            return;
        }
        SharedPreferences mySharedPreferences= context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mySharedPreferences.edit();
        String liststr =changeArrayDateToJson(list);
        edit.putString("MessageData",liststr);
        edit.commit();
    }
    //取出用户集合
    public static  ArrayList<MessageEntity> readaMessageData(Context context) {
        List=new ArrayList<MessageEntity>();
        SharedPreferences sharedPreferences=context.getSharedPreferences ("APP",  Context.MODE_PRIVATE);
        String liststr = sharedPreferences.getString("MessageData", "");
        try {
            JSONObject object=new JSONObject(liststr);
            String userDate=object.getString("Shortcut");
            JSONArray array=new JSONArray(userDate);

            for(int i=0;i<array.length();i++){
                JSONObject o=array.getJSONObject(i);
                MessageEntity entity=new MessageEntity();
                entity.setUserId(o.getString("uid"));
                entity.setCommentMessage(o.getString("comment"));
                entity.setOtherMessage(o.getString("other"));
                entity.setTaskAudit(o.getString("taskAudit"));
                entity.setTaskCompleted(o.getString("taskCompleted"));
                entity.setTaskUnfinished(o.getString("unfinished"));
                List.add(entity);
            }
            return List;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return List;
    }
    /**
     * 集合转换成json然后保存
     * @param list1
     */
    private static String  changeArrayDateToJson(java.util.List<MessageEntity> list1) { //把一个集合转换成json格式的字符串
        jsonArray=null;
        object=null;
        jsonArray = new JSONArray();
        object=new JSONObject();
        for (int i = 0; i < list1.size(); i++) { //遍历上面初始化的集合数据，把数据加入JSONObject里面
            object2 = new JSONObject();//一个user对象，使用一个JSONObject对象来装
            try {
                object2.put("uid", list1.get(i).getUserId()); //从集合取出数据，放入JSONObject里面 JSONObject对象和map差不多用法,以键和值形式存储数据
                object2.put("comment", list1.get(i).getCommentMessage());
                object2.put("other", list1.get(i).getOtherMessage());
                object2.put("taskAudit",list1.get(i).getTaskAudit());
                object2.put("taskCompleted",list1.get(i).getTaskCompleted());
                object2.put("unfinished",list1.get(i).getTaskUnfinished());
                ((JSONArray) jsonArray).put(object2); //把JSONObject对象装入jsonArray数组里面
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("Shortcut", jsonArray); //再把JSONArray数据加入JSONObject对象里面(数组也是对象)
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return	object.toString(); //把JSONObject转换成json格式的字符串
    }

    /**
     * 清理
     * @param context
     */
    public void clear(Context context){
        SharedPreferences pref = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();
    }
}
