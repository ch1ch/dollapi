package com.dollapi.servie;

import com.alibaba.fastjson.JSON;
import com.dollapi.domain.MachineInfo;
import com.dollapi.domain.OrderInfo;
import com.dollapi.domain.UserInfo;
import com.dollapi.exception.DollException;
import com.dollapi.mapper.MachineInfoMapper;
import com.dollapi.mapper.OrderInfoMapper;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.util.ApiContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<OrderInfo> getOrderList(Long userId) {
        List<OrderInfo> list = orderInfoMapper.selectByUserId(userId);
        return list;
    }


}
