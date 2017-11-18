package com.dollapi.util;

/**
 * Created by hexu on 2017/9/2.
 */
public enum ApiContents {

    NORMAL(200, "正常"),
    PARAMS_ERROR(1001, "参数错误"),
    SIGN_ERROR(1002, "签名错误"),
    WX_USER_NULL(2001, "微信没有找到此用户"),
    USER_GOME_MONEY_NULL(2002, "余额不足，请充值后再试"),
    USER_LOGIN_ERROR(2003, "用户身份信息错误，请从新登陆"),
    Invitation_ERROR(2004, "您已经被邀请过"),
    Invitation_CODE_ERROR(2005, "错误的邀请码"),
    MACHINE_USED(3001, "娃娃机游戏中，稍后再试"),
    CREATE_ORDER_ERROR(4001, "创建订单失败"),
    BACK_ORDER_ERROR(4002, "回调订单失败"),
    PACKAGE_ERROR(4003, "无效的充值套餐"),
    PUT_USER_LINE(4004, "已经加入当前机器排位，前位玩家结束后再游戏下单"),
    IS_YOUR(4005, "已经是当前玩家，请开始游戏"),
    LTS_ERROR(5001, "LTS错误");

    private Integer value;
    private String desc;

    ApiContents(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer value() {
        return this.value;
    }

    public String desc() {
        return this.desc;
    }

}
