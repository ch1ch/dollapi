package com.dollapi.domain;

import java.io.Serializable;

public class MachineInfo {

    public MachineInfo() {

    }

    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String machineName;

    /**
     *
     */
    private String introduce;

    private String dollName;

    private String dollIntroduce;

    private String dollImg;

    /**
     *
     */
    private String ipAddress;

    /**
     *
     */
    private String machineImg;

    /**
     *
     */
    private String video1;

    /**
     *
     */
    private String video2;

    /**
     *
     */
    private String video3;

    private Long gameMoney;

    /**
     *
     */
    private String commodityIds;

    /**
     *
     */
    private Integer status;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setMachineImg(String machineImg) {
        this.machineImg = machineImg;
    }

    public String getMachineImg() {
        return machineImg;
    }

    public void setVideo1(String video1) {
        this.video1 = video1;
    }

    public String getVideo1() {
        return video1;
    }

    public void setVideo2(String video2) {
        this.video2 = video2;
    }

    public String getVideo2() {
        return video2;
    }

    public void setVideo3(String video3) {
        this.video3 = video3;
    }

    public String getVideo3() {
        return video3;
    }

    public void setCommodityIds(String commodityIds) {
        this.commodityIds = commodityIds;
    }

    public String getCommodityIds() {
        return commodityIds;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public Long getGameMoney() {
        return gameMoney;
    }

    public void setGameMoney(Long gameMoney) {
        this.gameMoney = gameMoney;
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
}
