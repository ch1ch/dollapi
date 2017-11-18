package com.dollapi.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserInfo {

    public UserInfo() {

    }

    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String nickName;

    /**
     *
     */
    private Integer userLevel;

    /**
     *
     */
    private String phoneNumber;

    /**
     *
     */
    private Long userPoint;

    /**
     *
     */
    private String headUrl;

    /**
     *
     */
    private Integer gender;

    /**
     *
     */
    private Long gameMoney;

    /**
     *
     */
    private Long pushUserId;

    /**
     *
     */
    private Long dollCount;

    /**
     *
     */
    private Integer status;

    /**
     *
     */
    private String token;

    /**
     *
     */
    private Date createTime;

    private String invitationCode;

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUserPoint(Long userPoint) {
        this.userPoint = userPoint;
    }

    public Long getUserPoint() {
        return userPoint;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGameMoney(Long gameMoney) {
        this.gameMoney = gameMoney;
    }

    public Long getGameMoney() {
        return gameMoney;
    }

    public void setPushUserId(Long pushUserId) {
        this.pushUserId = pushUserId;
    }

    public Long getPushUserId() {
        return pushUserId;
    }

    public void setDollCount(Long dollCount) {
        this.dollCount = dollCount;
    }

    public Long getDollCount() {
        return dollCount;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }



}
