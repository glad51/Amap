package com.zz.demo.amap.bean;

/**
 * Created by zz on 2017/7/23.
 */

public class MessageEntity {
    private String name;
    private String address;
    private String adcode;

    public MessageEntity(String name, String adcode) {
        this.name = name;
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }
}
