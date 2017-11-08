package com.dollapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.pay.PayAPI;
import com.common.pay.PayParam;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Value("${aliAppPrivateKey}")
    private String aliAppPrivateKey;

    @Value("${aliAppPublicKey}")
    private String aliAppPublicKey;

    @Value("${aliPublicKey}")
    private String aliPublicKey;


    private static Map<Long, List<UserLine>> userLineMap = new HashMap<>();

//    private static boolean inGame = true;

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

            String url = "http://" + machineInfo.getIpAddress() + "/start?token=" + userInfo.getToken() + "&orderId=" + orderInfo.getId();
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httppost = new HttpGet(url);
            try {
                CloseableHttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseText = EntityUtils.toString(entity, "UTF-8");
                    JSONObject dataObject = JSON.parseObject(responseText);
                    if (!dataObject.get("code").toString().equals("200")) {
                        logger.info("创建订单失败:用户:" + JSON.toJSONString(userInfo) + "machineId:" + machineId.toString());
                        throw new DollException(ApiContents.CREATE_ORDER_ERROR.value(), ApiContents.CREATE_ORDER_ERROR.desc());
                    }
                }
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
//
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

//    private void putLine(Long machineId) throws InterruptedException {
//        if (!inGame) {
//            Thread.sleep(10000L);
//            List<UserLine> userLineList = userLineMap.get(machineId);
//            if (userLineList != null && userLineList.size() > 0) {
//                userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
//                userLineList.remove(0);
//                userLineMap.put(machineId, userLineList);
//                updateWilddogData(userLineMap, machineId);
//                putLine(machineId);
//            } else {
//                inGame = true;
//            }
//        }
//    }

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

    public String recharge(UserInfo user, Long packageId, Integer payType) {
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
        order.setOutPayOrder("0000");
        // FIXME: 2017/9/11 这里使用枚举 1微信 2支付宝
        order.setPayType(payType);
        // FIXME: 2017/10/25 这里使用枚举 1下单  2成功
        order.setStatus(1);
        rechargeOrderMapper.save(order);

//        user.setGameMoney(user.getGameMoney() + p.getGameMoney());
//        userInfoMapper.update(user);
        PayAPI api = PayAPI.instance().ali(aliAppId, aliAppPrivateKey, aliAppPublicKey, aliPublicKey, "json", "RSA");
//        api.notifyUrl("legendream.cn  成功回调");
        api.notifyUrl("http://47.94.236.45:9000/order/rechargeCallBack1");
        PayParam param = new PayParam();
        param.setSubject(p.getPackageName());
        param.setOutTradeNo(order.getId());
        param.setDesc(p.getPackageName() + "充值" + p.getPrice().toString() + "获得" + p.getGameMoney().toString() + "游戏币");

        String payInfo = api.pay(param, 10, "");
        return payInfo;
    }

    //暂时不用
    public String rechargePay(Long packageId) {
        RechargePackage rechargePackage = rechargePackageMapper.selectById(packageId);
        PayAPI api = PayAPI.instance().ali(aliAppId, aliAppPrivateKey, aliAppPublicKey, aliPublicKey, "json", "RSA");
        api.notifyUrl("legendream.cn");
        PayParam param = new PayParam();
        param.setSubject(rechargePackage.getPackageName());
        param.setOutTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
        param.setDesc(rechargePackage.getPackageName() + "充值" + rechargePackage.getPrice().toString() + "获得" + rechargePackage.getGameMoney().toString() + "游戏币");
        String payInfo = api.pay(param, 10, "");
        return payInfo;
    }

    public void rechargeCallBack(String orderId, String tradeNo) {
        // FIXME: 2017/10/24 这里需要实现
        RechargeOrder order = rechargeOrderMapper.selectById(orderId);
        UserInfo user = userInfoMapper.selectUserById(order.getUserId());
        order.setStatus(2);
        order.setOutPayOrder(tradeNo);

        user.setGameMoney(user.getGameMoney() + order.getGameMoney());
        userInfoMapper.update(user);
        rechargeOrderMapper.update(order);
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
                userLineList = new ArrayList<>();
                UserLine userLine = new UserLine();
                userLine.setUserId(userId);
                userLine.setCreateTime(new Date());
                userLineList.add(userLine);
                userLineMap.put(machineInfo.getId(), userLineList);

                updateWilddogData(userLineMap, machineInfo.getId());
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
//                updateWilddogData(userLineMap, machineInfo.getId());
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

    @Scheduled(cron = "0/10 * *  * * ? ")
    private void outLine() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Long key : userLineMap.keySet()) {
            MachineInfo machineInfo = machineInfoMapper.selectById(key);
            if (machineInfo.getStatus().equals(2)) {
                OrderInfo orderInfo = orderInfoMapper.selectOneByMachineId(key);
                if (orderInfo != null && (90 < ((new Date().getTime() - orderInfo.getCreateTime().getTime()) / 1000))) {
                    machineInfo.setStatus(1);
                    machineInfoMapper.update(machineInfo);
                    removeOne(key);
                }
            } else {
                List<UserLine> userLineList = userLineMap.get(key);
                if (userLineList != null && userLineList.size() > 0) {
                    userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
                    if (10 < ((new Date().getTime() - userLineList.get(0).getCreateTime().getTime()) / 1000)) {
                        System.out.println(sdf.format(new Date()));
                        removeOne(key);
                    }
                }
            }
        }
    }

    private void removeOne(Long machineId) {
        List<UserLine> userLineList = userLineMap.get(machineId);
        if (userLineList != null && userLineList.size() > 0) {
            userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
            userLineList.remove(0);
            userLineMap.put(machineId, userLineList);
            updateWilddogData(userLineMap, machineId);
        }
    }


}
