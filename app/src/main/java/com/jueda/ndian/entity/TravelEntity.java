package com.jueda.ndian.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/23.
 * 旅游
 */
public class TravelEntity implements Serializable {
    private String did;//出游id
    private String big_img="";
    private String title="";
    private String count;
    private String money="";//爱心豆
    private String comment_count="";//评论总数
    private String cost_price;//原价
    private String status;//
    private ArrayList<HashMap> content;//图文混排
    private String creat_time;//创建时间
    private String order;//订单号
    private String name;//购买者名称
    private String phone;//购买者手机号

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getCost_price() {
        return cost_price;
    }

    public void setCost_price(String cost_price) {
        this.cost_price = cost_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<HashMap> getContent() {
        return content;
    }

    public void setContent(ArrayList<HashMap> content) {
        this.content = content;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getBig_img() {
        return big_img;
    }

    public void setBig_img(String big_img) {
        this.big_img = big_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }
}
