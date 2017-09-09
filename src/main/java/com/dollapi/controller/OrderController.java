package com.dollapi.controller;

import com.dollapi.domain.OrderInfo;
import com.dollapi.servie.OrderService;
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
        validParamsNotNull(token);
        List<OrderInfo> list = orderService.getOrderList(getUserInfo(token).getId());
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }

}
