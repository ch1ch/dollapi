package com.dollapi.controller;

import com.dollapi.domain.OrderInfo;
import com.dollapi.domain.RechargeOrder;
import com.dollapi.domain.RechargePackage;
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
        orderService.createOrder(getUserInfo(token), machineId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
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
        orderService.recharge(getUserInfo(token), packageId, payType, outPayOrder);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

}
