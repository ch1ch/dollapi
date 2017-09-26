package com.dollapi.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderInfo {

    public OrderInfo() {

    }

    /**
     *
     */
    private String id;

    /**
     *
     */
    private Long userId;

    private String userName;

    /**
     *
     */
    private Long commodityId;

    private String commodityName;

    /**
     *
     */
    private Long machineId;

    private String machineName;

    /**
     *
     */
    private Long userAdressId;

    /**
     *
     */
    private Long gameMoneyPrice;

    private String dollName;

    private String dollIntroduce;

    private String dollImg;

    private String gameVideo;

    /**
     * 1为初始，游戏中，已经付款，2为抓取失败，3为抓取成功
     */
    private Integer status;

    /**
     *
     */
    private Date createTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setUserAdressId(Long userAdressId) {
        this.userAdressId = userAdressId;
    }

    public Long getUserAdressId() {
        return userAdressId;
    }

    public void setGameMoneyPrice(Long gameMoneyPrice) {
        this.gameMoneyPrice = gameMoneyPrice;
    }

    public Long getGameMoneyPrice() {
        return gameMoneyPrice;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getDollName() {
        return dollName;
    }

    public void setDollName(String dollName) {
        this.dollName = dollName;
    }

    public String getDollIntroduce() {
        return dollIntroduce;
    }

    public void setDollIntroduce(String dollIntroduce) {
        this.dollIntroduce = dollIntroduce;
    }

    public String getDollImg() {
        return dollImg;
    }

    public void setDollImg(String dollImg) {
        this.dollImg = dollImg;
    }

    public String getGameVideo() {
        return gameVideo;
    }

    public void setGameVideo(String gameVideo) {
        this.gameVideo = gameVideo;
    }
}
