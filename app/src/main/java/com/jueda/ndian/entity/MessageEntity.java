package com.jueda.ndian.entity;

/**
 * Created by Administrator on 2016/4/20.
 */
public class MessageEntity {
    private String name;//昵称
    private String avatar;//头像
    private String content;//内容
    private String from_uid;////消息来自用户uid
    private String cname;//圈名
    private String is_host;///是否为圈主
    private String id;//消息id
    private String cid;//消息所属圈子
    private String rid;//筹款id

    private String pid;//消息来自pid(type为1时才有)
    private String did;////消息来自did(type为2或5时才有)
    private String type;//消息类型 1: 话题评论 2:捐赠评论 3:加圈审核 4:移除圈 5:捐赠审核  11.筹款评论
    private String creat_time;//时间
    private String status;//0：未读 1：已读

    /**用户的消息是否已读*/
    private String userId;//谁的消息（确保切换账号后消息能够保存）
    private String commentMessage="";//评论的消息未读
    private String otherMessage="";//其余的消息未读

    /**任务消息*/
    private String taskAudit="";//审核中的任务
    private String taskCompleted="";//已完成的任务
    private String taskUnfinished="";//未完成

    public String getTaskAudit() {
        return taskAudit;
    }

    public void setTaskAudit(String taskAudit) {
        this.taskAudit = taskAudit;
    }

    public String getTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(String taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public String getTaskUnfinished() {
        return taskUnfinished;
    }

    public void setTaskUnfinished(String taskUnfinished) {
        this.taskUnfinished = taskUnfinished;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    public String getOtherMessage() {
        return otherMessage;
    }

    public void setOtherMessage(String otherMessage) {
        this.otherMessage = otherMessage;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(String from_uid) {
        this.from_uid = from_uid;
    }

    public String getIs_host() {
        return is_host;
    }

    public void setIs_host(String is_host) {
        this.is_host = is_host;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
