package com.dollapi.servie;

import com.alibaba.fastjson.JSON;
import com.dollapi.domain.*;
import com.dollapi.exception.DollException;
import com.dollapi.mapper.*;
import com.dollapi.util.ApiContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("orderService")
public class OrderService {
    private final static Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private MachineInfoMapper machineInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private RechargePackageMapper rechargePackageMapper;

    public void createOrder(UserInfo userInfo, Long machineId) {

        try {
            MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
            // FIXME: 2017/9/10 这里用枚举
            if (machineInfo.getStatus().equals(2)) {
                throw new DollException(ApiContents.MACHINE_USED.value(), ApiContents.MACHINE_USED.desc());
            }
            if (userInfo.getGameMoney() < machineInfo.getGameMoney()) {
                throw new DollException(ApiContents.USER_GOME_MONEY_NULL.value(), ApiContents.USER_GOME_MONEY_NULL.desc());
            } else {
                userInfo.setGameMoney(userInfo.getGameMoney() - machineInfo.getGameMoney());
            }

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            orderInfo.setUserId(userInfo.getId());
            orderInfo.setUserName(userInfo.getNickName());
            orderInfo.setMachineId(machineId);
            orderInfo.setMachineName(machineInfo.getMachineName());
            orderInfo.setGameMoneyPrice(machineInfo.getGameMoney());
            // FIXME: 2017/9/10 这里使用枚举
            orderInfo.setStatus(1);

            machineInfo.setStatus(2);

            userInfoMapper.update(userInfo);
            machineInfoMapper.update(machineInfo);
            orderInfoMapper.save(orderInfo);
        } catch (Exception e) {
            if (e instanceof DollException) {
                throw e;
            } else {
                logger.info("创建订单失败:用户:" + JSON.toJSONString(userInfo) + "machineId:" + machineId.toString());
                e.printStackTrace();
                throw new DollException(ApiContents.CREATE_ORDER_ERROR.value(), ApiContents.CREATE_ORDER_ERROR.desc());
            }

        }

    }

    public void callBack(UserInfo userInfo, Long machineId, Long orderId, Integer result) {
        try {
            MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
            // FIXME: 2017/9/10 这里用枚举
            machineInfo.setStatus(1);
            machineInfoMapper.update(machineInfo);

            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            // FIXME: 2017/9/10 这里使用枚举
            if (result == 1) {
                //抓到
                orderInfo.setStatus(3);
            } else {
                orderInfo.setStatus(2);
            }
            orderInfoMapper.update(orderInfo);
        } catch (Exception e) {
            if (e instanceof DollException) {
                throw e;
            } else {
                logger.info("订单回调失败:用户:" + JSON.toJSONString(userInfo) + "machineId:" + machineId.toString() + "orderId:" + orderId.toString() + "result:" + result.toString());
                e.printStackTrace();
                throw new DollException(ApiContents.BACK_ORDER_ERROR.value(), ApiContents.BACK_ORDER_ERROR.desc());
            }
        }

    }

    public List<OrderInfo> getOrderList(Long userId, Integer doll) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("doll", doll);
        List<OrderInfo> list = orderInfoMapper.selectByUserId(params);
        return list;
    }

    public List<RechargePackage> getRechargePackage() {
        List<RechargePackage> list = rechargePackageMapper.selectAllPackage();
        return list;
    }

    public List<RechargeOrder> getRechargeOrderByUserId(Long userId) {
        List<RechargeOrder> list = rechargeOrderMapper.selectByUserId(userId);
        return list;
    }

    public void recharge(UserInfo user, Long packageId, Integer payType, String outPayOrder) {
        RechargePackage p = rechargePackageMapper.selectById(packageId);
        if (p == null || p.getId() == null) {
            throw new DollException(ApiContents.PACKAGE_ERROR.value(), ApiContents.PACKAGE_ERROR.desc());
        }
        RechargeOrder order = new RechargeOrder();
        order.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        order.setUserId(user.getId());
        order.setUserName(user.getNickName());
        order.setPackageId(p.getId());
        order.setPackageName(p.getPackageName());
        order.setPrice(p.getPrice());
        order.setGameMoney(p.getGameMoney());
        order.setOutPayOrder(outPayOrder);
        // FIXME: 2017/9/11 这里使用枚举 1微信 2支付宝
        order.setPayType(payType);
        order.setStatus(1);
        rechargeOrderMapper.save(order);
        user.setGameMoney(user.getGameMoney() + p.getGameMoney());
        userInfoMapper.update(user);
    }


}
