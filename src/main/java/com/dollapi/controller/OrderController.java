package com.dollapi.controller;

import com.common.pay.PayAPI;
import com.common.pay.PayResult;
import com.common.pay.common.JSON;
import com.dollapi.domain.*;
import com.dollapi.mapper.ExpressMapper;
import com.dollapi.mapper.OrderInfoMapper;
import com.dollapi.service.OrderService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import com.dollapi.vo.OrderVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.jndi.cosnaming.ExceptionMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private ExpressMapper expressMapper;

    @RequestMapping("/createOrder")
    public Results createOrder(HttpServletRequest request) {
        String token = request.getParameter("token");
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId").toString());
        validParamsNotNull(token, machineId);
        String orderId = orderService.createOrder(getUserInfo(token), machineId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), orderId);
    }

    @RequestMapping("/canCreateOrder")
    public Results canCreateOrder(HttpServletRequest request) {
        String token = request.getParameter("token");
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId").toString());
        validParamsNotNull(token, machineId);
        orderService.canCreateOrder(getUserInfo(token), machineId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

    @RequestMapping("/addExpress")
    public Results addExpress(HttpServletRequest request) {
        String token = request.getParameter("token");
        Long adressId = request.getParameter("adressId") == null ? null : Long.valueOf(request.getParameter("adressId").toString());
        String orderId = request.getParameter("orderId");

        validParamsNotNull(token, adressId, orderId);
        Express express = new Express();
        express.setOrderId(orderId);
        express.setAdressId(adressId);
        express.setUserId(getUserInfo(token).getId());
        express.setStatus(1);
        expressMapper.save(express);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

    @RequestMapping("/getExpressByUserId")
    public Results getExpressByUserId(HttpServletRequest request) {
        String token = request.getParameter("token");
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<Express> list = expressMapper.selectByUserId(getUserInfo(token).getId());
        PageInfo pageInfo = new PageInfo(list);
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("rows", pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("nextPage", pageInfo.getNextPage());
        map.put("prePage", pageInfo.getPrePage());
        map.put("numbers", numbers);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), map);
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

    @RequestMapping("/getOrderByMachineId")
    public Results getOrderByMachineId(HttpServletRequest request) {
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId"));
        Integer page = request.getParameter("page") == null ? null : Integer.valueOf(request.getParameter("page"));
        validParamsNotNull(machineId, page);
        Map<String, Object> map = new HashedMap();
        map = orderService.getOrderByMachineId(machineId, page);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), map);
    }

}
