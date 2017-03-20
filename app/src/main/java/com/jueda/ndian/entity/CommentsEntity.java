package com.jueda.ndian.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/26.
 * 评论
 */
public class CommentsEntity implements Serializable{
    /**评论*/
    private String count_comments;//评论总数
    private String pid;//评论id
    private String puid;//评论者id
    private String pname;//评论人昵称
    private String pavatar;//评论者头像
    private String pcontent;//评论内容
    private String pcreat_time;//评论时间
    private String by_id;//0:一级评论 非0：回复评论id
    private String by_uname;//回复的用户昵称
    private String by_uid;//回复的用户id

    public String getCount_comments() {
        return count_comments;
    }

    public void setCount_comments(String count_comments) {
        this.count_comments = count_comments;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPavatar() {
        return pavatar;
    }

    public void setPavatar(String pavatar) {
        this.pavatar = pavatar;
    }

    public String getPcontent() {
        return pcontent;
    }

    public void setPcontent(String pcontent) {
        this.pcontent = pcontent;
    }

    public String getPcreat_time() {
        return pcreat_time;
    }

    public void setPcreat_time(String pcreat_time) {
        this.pcreat_time = pcreat_time;
    }

    public String getBy_id() {
        return by_id;
    }

    public void setBy_id(String by_id) {
        this.by_id = by_id;
    }

    public String getBy_uname() {
        return by_uname;
    }

    public void setBy_uname(String by_uname) {
        this.by_uname = by_uname;
    }

    public String getBy_uid() {
        return by_uid;
    }

    public void setBy_uid(String by_uid) {
        this.by_uid = by_uid;
    }
}
