package com.jueda.ndian.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 商品信息
 * Created by Administrator on 2016/6/25.
 */
public class CommodityEntity implements Serializable{
    private String id;//商品id
    private String order_id;//订单id
    private String gid;//订单跳转到详情需要的id
    private String title;//标题
    private String price;//价格
    private String total_fee;//总金额
    private String consignee;//收货人
    private String phone;//收货人手机号
    private String address;//收货人地址
    private String remark;//收货备注信息
    private String order_no;//订单号
    private String freightage;//运费
    private String old_price;//原价
    private String creat_time;//时间
    private String img;//图片
    private String imgs[];//图片
    private int imgs_height[];//图片的高度
    private String bead;//爱心豆
    private ArrayList<Photo> photoArrayList;//图片查看需要\
    private String commission;//佣金
    private String official_or_personal;//2官方或1个人
    private String paid_money;//已支付金额
    private String sales;//销量


    private String content;//介绍内容
    private String goodsState;//收货状态 1/4/3表示收货 2表示待收货

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getPaid_money() {
        return paid_money;
    }

    public void setPaid_money(String paid_money) {
        this.paid_money = paid_money;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(String goodsState) {
        this.goodsState = goodsState;
    }

    public String getOfficial_or_personal() {
        return official_or_personal;
    }

    public void setOfficial_or_personal(String official_or_personal) {
        this.official_or_personal = official_or_personal;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getBead() {
        return bead;
    }

    public void setBead(String bead) {
        this.bead = bead;
    }

    public ArrayList<Photo> getPhotoArrayList() {
        return photoArrayList;
    }

    public void setPhotoArrayList(ArrayList<Photo> photoArrayList) {
        this.photoArrayList = photoArrayList;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFreightage() {
        return freightage;
    }

    public void setFreightage(String freightage) {
        this.freightage = freightage;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public int[] getImgs_height() {
        return imgs_height;
    }

    public void setImgs_height(int[] imgs_height) {
        this.imgs_height = imgs_height;
    }
}
