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
import com.dollapi.vo.OrderExpress;
import com.dollapi.vo.UserLine;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


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

    @Autowired
    private ExpressMapper expressMapper;

    private List<MachineInfo> allMachineInfo = null;

    @Value("${aliAppId}")
    private String aliAppId;

    @Value("${aliAppPrivateKey}")
    private String aliAppPrivateKey;

    @Value("${aliAppPublicKey}")
    private String aliAppPublicKey;

    @Value("${aliPublicKey}")
    private String aliPublicKey;

    @Autowired
    private UserAdressMapper userAdressMapper;

//    private static Map<Long, List<UserLine>> userLineMap = new HashMap<>();

//    private static boolean inGame = true;

    public String createOrder(UserInfo userInfo, Long machineId) {

        String orderId = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
//            isUserLine(userInfo.getId(), machineInfo);
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
            orderInfo.setDollImg(machineInfo.getDollImg());
            orderInfo.setDollName(machineInfo.getDollName());

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

    public boolean canCreateOrder(UserInfo userInfo, Long machineId) {
        MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
        if (machineInfo.getStatus().equals(2)) {
            throw new DollException(ApiContents.MACHINE_USED.value(), ApiContents.MACHINE_USED.desc());
        }
        if (userInfo.getGameMoney() < machineInfo.getGameMoney()) {
            throw new DollException(ApiContents.USER_GOME_MONEY_NULL.value(), ApiContents.USER_GOME_MONEY_NULL.desc());
        }
        return true;
    }

    //    @Transactional
    public void callBack(UserInfo userInfo, Long machineId, String orderId, Integer result) {
        try {
            MachineInfo machineInfo = machineInfoMapper.selectById(machineId);
            // FIXME: 2017/9/10 这里用枚举
            machineInfo.setStatus(1);
//            machineInfoMapper.update(machineInfo);

            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            // FIXME: 2017/9/10 这里使用枚举
            if (orderInfo.getStatus().equals(3) || orderInfo.getStatus() > 3) {
                //不能改
            } else {
                if (result == 3) {
                    //抓到
                    orderInfo.setStatus(3);
                    orderInfoMapper.update(orderInfo);
                    userInfo.setDollCount(userInfo.getDollCount() + 1);
                    userInfoMapper.update(userInfo);
                }
//                } else {
//                    orderInfo.setStatus(2);
//                    orderInfoMapper.update(orderInfo);
//                }
            }


//            List<UserLine> userLineList = userLineMap.get(machineInfo.getId());
//            if (userLineList != null && userLineList.size() > 0) {
//                userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
//                userLineList.remove(0);
//                userLineMap.put(machineId, userLineList);
//                updateWilddogData(userLineMap, machineId);
////
//            }


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
        api.notifyUrl("http://47.94.236.45:9000/order/rechargeCallBack");
//        api.notifyUrl("http://47.94.236.45:9900/order/rechargeCallBack");
        PayParam param = new PayParam();
        param.setSubject(p.getPackageName());
        param.setOutTradeNo(order.getId());
        param.setDesc(p.getPackageName() + "充值" + p.getPrice().toString() + "获得" + p.getGameMoney().toString() + "游戏币");
//        param.setMoney("0.01");
        param.setMoney(p.getPrice().toString());

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
        if (order.getStatus().equals(2) || !order.getOutPayOrder().equals("0000")) {

        } else {
            UserInfo user = userInfoMapper.selectUserById(order.getUserId());
            order.setStatus(2);
            order.setOutPayOrder(tradeNo);


            //首冲活动
            Long add = 0L;
            List<RechargeOrder> rechargeOrderList = rechargeOrderMapper.selectByUserId(user.getId());
            if (rechargeOrderList == null || rechargeOrderList.size() == 1) {
                add = act(order);
            } else {
                rechargeOrderList = rechargeOrderList.stream().filter(e -> e.getPackageId().equals(order.getPackageId())).filter(e -> e.getStatus().equals(2)).collect(Collectors.toList());
                if (rechargeOrderList.size() < 1) {
                    add = act(order);
                }
            }

            user.setGameMoney(user.getGameMoney() + order.getGameMoney() + add);
            userInfoMapper.update(user);
            rechargeOrderMapper.update(order);
        }
    }

    private Long act(RechargeOrder order) {
        Long add = 0L;
        if (order.getGameMoney() == 100) {
            add = 100L;
        } else if (order.getGameMoney() == 200) {
            add = 30L;
        } else if (order.getGameMoney() == 500) {
            add = 80L;
        } else if (order.getGameMoney() == 1000) {
            add = 190L;
        } else if (order.getGameMoney() == 3000) {
            add = 620L;
        } else if (order.getGameMoney() == 5000) {
            add = 1100L;
        }
        return add;
    }

//    private void isUserLine(Long userId, MachineInfo machineInfo) {
//        List<UserLine> userLineList = userLineMap.get(machineInfo.getId());
//        if (userLineList == null || userLineList.size() == 0) {
//            //队列为空
//            if (machineInfo.getStatus().equals(2)) {
//                //游戏机使用中，加入队列，更新野狗数据
//                userLineList = new ArrayList<>();
//                UserLine userLine = new UserLine();
//                userLine.setUserId(userId);
//                userLine.setCreateTime(new Date());
//                userLineList.add(userLine);
//                userLineMap.put(machineInfo.getId(), userLineList);
//
//                updateWilddogData(userLineMap, machineInfo.getId());
//
//                throw new DollException(ApiContents.PUT_USER_LINE.value(), ApiContents.PUT_USER_LINE.desc());
//            } else {
//                userLineList = new ArrayList<>();
//                UserLine userLine = new UserLine();
//                userLine.setUserId(userId);
//                userLine.setCreateTime(new Date());
//                userLineList.add(userLine);
//                userLineMap.put(machineInfo.getId(), userLineList);
//
//                updateWilddogData(userLineMap, machineInfo.getId());
//                return;
//            }
//        }
//        userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
//        if (!userId.equals(userLineList.get(0).getUserId())) {
//            //不是当前玩家
//            boolean haveUser = false;
//            for (UserLine userLine : userLineList) {
//                if (userLine.getUserId().equals(userId)) {
//                    haveUser = true;
//                }
//            }
//
//            if (!haveUser) {
//                //加入队列,更新野狗数据
//                UserLine thisUser = new UserLine();
//                thisUser.setUserId(userId);
//                thisUser.setCreateTime(new Date());
//                userLineList.add(thisUser);
//                userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
//                updateWilddogData(userLineMap, machineInfo.getId());
//                userLineMap.put(machineInfo.getId(), userLineList);
//            }
//
//            throw new DollException(ApiContents.PUT_USER_LINE.value(), ApiContents.PUT_USER_LINE.desc());
//        } else {
//            //是当前玩家
//            if (machineInfo.getStatus().equals(2)) {
//                //游戏中
//                throw new DollException(ApiContents.IS_YOUR.value(), ApiContents.IS_YOUR.desc());
//            } else {
//                //当前玩家进入游戏
////                userLineList.remove(0);
////                userLineMap.put(machineInfo.getId(), userLineList);
////                updateWilddogData(userLineMap, machineInfo.getId());
//            }
//        }
//    }

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
        System.out.println(111111);
        allMachineInfo = machineInfoMapper.selectAll();
        for (MachineInfo machineInfo : allMachineInfo) {
            if (machineInfo.getStatus().equals(2)) {
                OrderInfo orderInfo = orderInfoMapper.selectOneByMachineId(machineInfo.getId());
                if (orderInfo != null && (70 < ((new Date().getTime() - orderInfo.getCreateTime().getTime()) / 1000))) {
                    machineInfo.setStatus(1);
                    machineInfoMapper.update(machineInfo);
//                    orderInfo.setStatus(2);
//                    orderInfoMapper.update(orderInfo);
                }
            }
        }
    }

    @Scheduled(cron = "0 0/10 * * * ? ")
    private void updataAllMachineInfo() {
        allMachineInfo = machineInfoMapper.selectAll();
    }


//
//    private void removeOne(Long machineId) {
//        List<UserLine> userLineList = userLineMap.get(machineId);
//        if (userLineList != null && userLineList.size() > 0) {
//            userLineList.sort((UserLine l1, UserLine l2) -> l1.getCreateTime().compareTo(l2.getCreateTime()));
//            userLineList.remove(0);
//            userLineMap.put(machineId, userLineList);
//            updateWilddogData(userLineMap, machineId);
//        }
//    }

    public Map getOrderByMachineId(Long machineId, Integer page) {
        Map<String, Object> params = new HashedMap();
        if (machineId != null && !machineId.equals("")) {
            params.put("machineId", machineId);
        }
        if (page == null) {
            page = 1;
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<OrderInfo> list = orderInfoMapper.selectByMachineForPage(params);
        PageInfo pageInfo = new PageInfo(list);
        Map<String, Object> map = new HashedMap();
        map.put("data", pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("nextPage", pageInfo.getNextPage());
        map.put("prePage", pageInfo.getPrePage());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    public List<OrderExpress> toExpress(List<Express> expressList) {
        List<OrderExpress> list = new ArrayList<>();


        for (Express express : expressList) {
            OrderInfo order = orderInfoMapper.selectById(express.getOrderId());
            if (order != null) {
                OrderExpress orderExpress = new OrderExpress();
                BeanUtils.copyProperties(order, orderExpress);
                orderExpress.setId(express.getId());
                orderExpress.setExpressOutOrderId(express.getOutOrderId());
                orderExpress.setExpressStatus(express.getStatus());
                orderExpress.setOrderId(express.getOrderId());
                orderExpress.setCreateTime(express.getCreateTime());


                UserAdress userAdress = userAdressMapper.selectById(express.getAdressId());

                orderExpress.setPerson(userAdress.getPerson());
                orderExpress.setMobile(userAdress.getMobile());
                orderExpress.setAddress(userAdress.getAddress());
                list.add(orderExpress);
            }
        }

        return list;

    }


}
