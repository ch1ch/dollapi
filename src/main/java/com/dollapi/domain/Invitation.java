package com.dollapi.domain;

import java.util.Date;

public class Invitation {
    private Long userId;
    private Long recommendUserId;
    private Long gameMony;
    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRecommendUserId() {
        return recommendUserId;
    }

    public void setRecommendUserId(Long recommendUserId) {
        this.recommendUserId = recommendUserId;
    }

    public Long getGameMony() {
        return gameMony;
    }

    public void setGameMony(Long gameMony) {
        this.gameMony = gameMony;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
