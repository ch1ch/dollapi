package com.dollapi.vo;

import java.util.Date;

public class OrderVO {

    private String id;
    private Long gameMoneyPrice;

    private String dollName;
    private String dollImg;
    private Integer status;

    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getGameMoneyPrice() {
        return gameMoneyPrice;
    }

    public void setGameMoneyPrice(Long gameMoneyPrice) {
        this.gameMoneyPrice = gameMoneyPrice;
    }

    public String getDollName() {
        return dollName;
    }

    public void setDollName(String dollName) {
        this.dollName = dollName;
    }

    public String getDollImg() {
        return dollImg;
    }

    public void setDollImg(String dollImg) {
        this.dollImg = dollImg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
