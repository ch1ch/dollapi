package com.dollapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.pay.PayAPI;
import com.dollapi.domain.*;
import com.dollapi.exception.DollException;
import com.dollapi.mapper.*;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import com.dollapi.vo.UserLine;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


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

    @Value("${aliAppId}")
    private String aliAppId;

    @Value("${aliAppId}")
    private String aliAppId;

    private static Map<Long, List<UserLine>> userLineMap = new HashMap<>();

    private static boolean inGame = true;

    public String createOrder(UserInfo userInfo, Long machineId) {

        String orderId = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
            isUserLine(userInfo.getId(), machineInfo);
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
            orderInfo.setId(orderId);
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

            String url = machineInfo.getIpAddress() + "/start?token=" + userInfo.getToken() + "&orderId=" + orderInfo.getId();
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httppost = new HttpGet(url);
            try {
                CloseableHttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseText = EntityUtils.toString(entity, "UTF-8");
                    JSONObject dataObject = JSON.parseObject(responseText);
                    if (!dataObject.get("code").equals("200")) {
                        logger.info("创建订单失败:用户:" + JSON.toJSONString(userInfo) + "machineId:" + machineId.toString());
                        throw new DollException(ApiContents.CREATE_ORDER_ERROR.value(), ApiContents.CREATE_ORDER_ERROR.desc());
                    }
                }
                inGame = true;
                return orderId;
            } catch (IOException y) {
                logger.info("创建订单失败:用户:" + JSON.toJSONString(userInfo) + "machineId:" + machineId.toString());
                y.printStackTrace();
                throw new DollException(ApiContents.CREATE_ORDER_ERROR.value(), ApiContents.CREATE_ORDER_ERROR.desc());
            }
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

    public void callBack(UserInfo userInfo, Long machineId, String orderId, Integer result) {
        try {
            MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
            // FIXME: 2017/9/10 这里用枚举
            machineInfo.setStatus(1);
            machineInfoMapper.update(machineInfo);

            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            // FIXME: 2017/9/10 这里使用枚举
            if (result == 3) {
                //抓到
                orderInfo.setStatus(3);
            } else {
                orderInfo.setStatus(2);
            }
            orderInfoMapper.update(orderInfo);

            List<UserLine> userLineList = userLineMap.get(machineInfo.getId());
            if (userLineList != null && userLineList.size() > 0) {
                userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
                userLineList.remove(0);
                userLineMap.put(machineId, userLineList);
                updateWilddogData(userLineMap, machineId);
                inGame = false;
                new Thread() {
                    public void run() {
                        try {
                            putLine(machineId);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }


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

    private void putLine(Long machineId) throws InterruptedException {
        if (!inGame) {
            Thread.sleep(10000L);
            List<UserLine> userLineList = userLineMap.get(machineId);
            if (userLineList != null && userLineList.size() > 0) {
                userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
                userLineList.remove(0);
                userLineMap.put(machineId, userLineList);
                updateWilddogData(userLineMap, machineId);
                putLine(machineId);
            } else {
                inGame = true;
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

    public void rechargePay(Long packageId) {
        RechargePackage rechargePackage = rechargePackageMapper.selectById(packageId);
        PayAPI api = PayAPI.instance().ali();
        api.pay()
    }

    private void isUserLine(Long userId, MachineInfo machineInfo) {
        List<UserLine> userLineList = userLineMap.get(machineInfo.getId());
        if (userLineList == null || userLineList.size() == 0) {
            //队列为空
            if (machineInfo.getStatus().equals(2)) {
                //游戏机使用中，加入队列，更新野狗数据
                userLineList = new ArrayList<>();
                UserLine userLine = new UserLine();
                userLine.setUserId(userId);
                userLine.setCreateTime(new Date());
                userLineList.add(userLine);
                userLineMap.put(machineInfo.getId(), userLineList);

                updateWilddogData(userLineMap, machineInfo.getId());

                throw new DollException(ApiContents.PUT_USER_LINE.value(), ApiContents.PUT_USER_LINE.desc());
            } else {
                return;
            }
        }
        userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
        if (!userId.equals(userLineList.get(0).getUserId())) {
            //不是当前玩家
            boolean haveUser = false;
            for (UserLine userLine : userLineList) {
                if (userLine.getUserId().equals(userId)) {
                    haveUser = true;
                }
            }

            if (!haveUser) {
                //加入队列,更新野狗数据
                UserLine thisUser = new UserLine();
                thisUser.setUserId(userId);
                thisUser.setCreateTime(new Date());
                userLineList.add(thisUser);
                userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
                updateWilddogData(userLineMap, machineInfo.getId());
                userLineMap.put(machineInfo.getId(), userLineList);
            }

            throw new DollException(ApiContents.PUT_USER_LINE.value(), ApiContents.PUT_USER_LINE.desc());
        } else {
            //是当前玩家
            if (machineInfo.getStatus().equals(2)) {
                //游戏中
                throw new DollException(ApiContents.PUT_USER_LINE.value(), ApiContents.PUT_USER_LINE.desc());
            } else {
                //当前玩家进入游戏
//                userLineList.remove(0);
//                userLineMap.put(machineInfo.getId(), userLineList);
//                updateWilddogData(userLineMap);
            }
        }
    }

    private void updateWilddogData(Map<Long, List<UserLine>> userLineMap, Long machineId) {
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd2620361786fgzrcs.wilddogio.com").build();
        WilddogApp.initializeApp(options);
        SyncReference ref = WilddogSync.getInstance().getReference();
        List<UserLine> userLineList = userLineMap.get(machineId);
        HashMap<String, Object> map = new HashMap<>();
        for (int i = userLineList.size() - 1; i >= 0; i--) {
            map.put(String.valueOf(i + 1), userLineList.get(i).getUserId());
        }
        ref.child(machineId.toString()).setValue(map);
    }


}
