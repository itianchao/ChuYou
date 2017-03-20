package com.jueda.ndian.entity;

import java.io.File;

/**
 * 下载
 * Created by Administrator on 2016/3/29.
 */
public class serverAppEntity {
    private int key;//记录第几个item
    private String name;
    private File file;
    private String who;//判断是在首页点击下载的还是圈子里面下载的

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
