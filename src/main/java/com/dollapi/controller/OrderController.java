package com.dollapi.controller;

import com.common.pay.PayAPI;
import com.common.pay.PayResult;
import com.dollapi.domain.OrderInfo;
import com.dollapi.domain.RechargeOrder;
import com.dollapi.domain.RechargePackage;
import com.dollapi.domain.UserInfo;
import com.dollapi.service.OrderService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/createOrder")
    public Results createOrder(HttpServletRequest request) {
        String token = request.getParameter("token");
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId").toString());
        validParamsNotNull(token, machineId);
        String orderId = orderService.createOrder(getUserInfo(token), machineId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), orderId);
    }

    @RequestMapping("/getUserOrder")
    public Results getUserOrder(HttpServletRequest request) {
        String token = request.getParameter("token");
        Integer doll = request.getParameter("doll") == null ? null : Integer.valueOf(request.getParameter("doll").toString());
        validParamsNotNull(token);
        List<OrderInfo> list = orderService.getOrderList(getUserInfo(token).getId(), doll);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }

    @RequestMapping("/getRechargePackage")
    public Results getRechargePackage() {
        List<RechargePackage> list = orderService.getRechargePackage();
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }

    @RequestMapping("/getRechargeOrderByUserId")
    public Results getRechargeOrderByUserId(HttpServletRequest request) {
        String token = request.getParameter("token");
        validParamsNotNull(token);
        List<RechargeOrder> list = orderService.getRechargeOrderByUserId(getUserInfo(token).getId());
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }

    @RequestMapping("/recharge")
    public Results recharge(HttpServletRequest request) {
        String token = request.getParameter("token");
        Long packageId = request.getParameter("packageId") == null ? null : Long.valueOf(request.getParameter("packageId").toString());
        Integer payType = request.getParameter("payType") == null ? null : Integer.valueOf(request.getParameter("payType").toString());
        String outPayOrder = request.getParameter("outPayOrder");
        validParamsNotNull(token, packageId, payType, outPayOrder);
        String payInfo = orderService.recharge(getUserInfo(token), packageId, payType, outPayOrder);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), payInfo);
    }


    @RequestMapping("/rechargeCallBack")
    public Results rechargeCallBack(HttpServletRequest request) {
        PayResult r = PayAPI.instance().processNotify(request.getParameterMap(), 1);
        if (r.getStatus() == PayResult.PayStatus.success){
            //支付成功
            r.getTradeNo();//支付宝流水号
            r.getOrderCode();//我方订单号
        }
            return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

    @RequestMapping("/callBack")
    public Results callBack(HttpServletRequest request) {
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId").toString());
        String orderId = request.getParameter("orderId") == null ? null : request.getParameter("orderId").toString();
        Integer result = request.getParameter("result") == null ? null : Integer.valueOf(request.getParameter("result").toString());
        String token = request.getParameter("token");
        UserInfo user = getUserInfo(token);
        orderService.callBack(user, machineId, orderId, result);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

}
