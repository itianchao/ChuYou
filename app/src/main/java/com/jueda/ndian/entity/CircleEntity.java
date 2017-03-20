package com.jueda.ndian.entity;

import java.io.Serializable;

/**
 * 圈的信息
 * Created by Administrator on 2016/3/31.
 */
public class CircleEntity implements Serializable{
    private String name;//圈子名称
    private String cid;//圈子id
    private String cdesc;//圈子简介
    private String avatar;//圈子头像
    private String count;//圈子浏览数
    private String is_member;//是否为该圈成员
    private String owner_id;//圈主id
    private String owner_name;//圈主昵称
    private String owner_avatar;//圈主头像
    private String c_member;//圈子成员总数
    private String invite_code;//圈子邀请码
    private String add_set;//圈子是否允许外人加入
    private String is_host;//是否为圈主
    private String is_collect;//是否收藏该圈子
    private String devotion;//该圈总奉献值
    private String bean;//圈子爱心豆
    private String is_creater;//是否是本账号创建的 1是 0不是

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getIs_creater() {
        return is_creater;
    }

    public void setIs_creater(String is_creater) {
        this.is_creater = is_creater;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_avatar() {
        return owner_avatar;
    }

    public void setOwner_avatar(String owner_avatar) {
        this.owner_avatar = owner_avatar;
    }

    public String getC_member() {
        return c_member;
    }

    public void setC_member(String c_member) {
        this.c_member = c_member;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getAdd_set() {
        return add_set;
    }

    public void setAdd_set(String add_set) {
        this.add_set = add_set;
    }

    public String getIs_host() {
        return is_host;
    }

    public void setIs_host(String is_host) {
        this.is_host = is_host;
    }

    public String getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(String is_collect) {
        this.is_collect = is_collect;
    }

    public String getDevotion() {
        return devotion;
    }

    public void setDevotion(String devotion) {
        this.devotion = devotion;
    }

    public String getIs_member() {
        return is_member;
    }

    public void setIs_member(String is_member) {
        this.is_member = is_member;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
