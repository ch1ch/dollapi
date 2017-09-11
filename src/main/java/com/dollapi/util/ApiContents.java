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
    MACHINE_USED(3001, "娃娃机不可用，稍后再试"),
    CREATE_ORDER_ERROR(4001, "创建订单失败"),
    BACK_ORDER_ERROR(4002, "回调订单失败"),
    PACKAGE_ERROR(4003, "无效的充值套餐");

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
