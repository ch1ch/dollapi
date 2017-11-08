package com.dollapi.controller;

import com.common.pay.PayAPI;
import com.common.pay.PayResult;
import com.common.pay.common.JSON;
import com.dollapi.domain.OrderInfo;
import com.dollapi.domain.RechargeOrder;
import com.dollapi.domain.RechargePackage;
import com.dollapi.domain.UserInfo;
import com.dollapi.mapper.OrderInfoMapper;
import com.dollapi.service.OrderService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import com.dollapi.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayAPI api;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @RequestMapping("/createOrder")
    public Results createOrder(HttpServletRequest request) {
        String token = request.getParameter("token");
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId").toString());
        validParamsNotNull(token, machineId);
        String orderId = orderService.createOrder(getUserInfo(token), machineId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), orderId);
    }

    @RequestMapping("/getOrderById")
    public Results getOrderById(HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        validParamsNotNull(orderId);
        OrderInfo order = orderInfoMapper.selectById(orderId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), order);
    }

    @RequestMapping("/getUserOrder")
    public Results getUserOrder(HttpServletRequest request) {
        String token = request.getParameter("token");
        Integer doll = request.getParameter("doll") == null ? null : Integer.valueOf(request.getParameter("doll").toString());
        validParamsNotNull(token);
        List<OrderInfo> list = orderService.getOrderList(getUserInfo(token).getId(), doll);
        List<OrderVO> voList = new ArrayList<>();
        for (OrderInfo orderInfo : list) {
            OrderVO vo = new OrderVO();
            vo.setId(orderInfo.getId());
            vo.setGameMoneyPrice(orderInfo.getGameMoneyPrice());
            vo.setDollName(orderInfo.getDollName());
            vo.setDollImg(orderInfo.getDollImg());
            vo.setStatus(orderInfo.getStatus());
            vo.setCreateTime(orderInfo.getCreateTime());
            voList.add(vo);
        }
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), voList);
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
//        String outPayOrder = request.getParameter("outPayOrder");


        validParamsNotNull(token, packageId, payType);
        String payInfo = orderService.recharge(getUserInfo(token), packageId, payType);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), payInfo);
    }


    @RequestMapping("/rechargeCallBack1")
    public Results rechargeCallBack(@RequestParam(required = false) Map<String, String> map) {
        logger.info("===================================收到支付回调===================================" + JSON.toJSONStr(map));
        PayResult r = api.processNotify(map, 1);
        if (r.getStatus() == PayResult.PayStatus.success) {
            //支付成功
//            r.getTradeNo();//支付宝流水号
//            r.getOrderCode();//我方订单号
            orderService.rechargeCallBack(r.getOrderCode(), r.getTradeNo());
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
