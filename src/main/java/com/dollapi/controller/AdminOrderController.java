package com.dollapi.controller;

import com.dollapi.domain.Express;
import com.dollapi.domain.OrderInfo;
import com.dollapi.domain.RechargeOrder;
import com.dollapi.domain.RechargePackage;
import com.dollapi.mapper.ExpressMapper;
import com.dollapi.mapper.OrderInfoMapper;
import com.dollapi.mapper.RechargeOrderMapper;
import com.dollapi.mapper.RechargePackageMapper;
import com.dollapi.service.OrderService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import com.dollapi.vo.OrderExpress;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Copyright: All Rights Reserved</p>
 * <p>Company: 指点无限(北京)科技有限公司   http://www.zhidianwuxian.cn</p>
 * <p>Description:  </p>
 * <p>Author:hexu/方和煦, 2017/10/27</p>
 */

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RechargePackageMapper rechargePackageMapper;

    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private ExpressMapper expressMapper;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/orderList")
    public String orderList(ModelMap map, HttpServletRequest request) {
        Long userId = request.getParameter("userId") == null || request.getParameter("userId").toString().equals("") ? null : Long.valueOf(request.getParameter("userId"));
        String id = request.getParameter("id") == null ? null : request.getParameter("id");

        Map<String, Object> params = new HashedMap();
        if (userId != null && userId > 0) {
            params.put("userId", userId);
        }
        if (id != null && !id.equals("")) {
            params.put("id", id);
        }

        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<OrderInfo> list = orderInfoMapper.selectAllOrder(params);
        PageInfo pageInfo = new PageInfo(list);
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }

        map.addAttribute("list", pageInfo.getList());
        map.addAttribute("pageNum", pageInfo.getPageNum());
        map.addAttribute("nextPage", pageInfo.getNextPage());
        map.addAttribute("prePage", pageInfo.getPrePage());
        map.addAttribute("numbers", numbers);
        map.put("date", new DateTool());
        return "orderList";
    }

    @RequestMapping("/getOrderById")
    public String getOrderById(ModelMap map, HttpServletRequest request) {
        String orderId = request.getParameter("id");
        OrderInfo order = orderInfoMapper.selectById(orderId);
        map.addAttribute("order", order);
        map.put("date", new DateTool());
        return "orderInfo";
    }


    @RequestMapping("/rechargePackageList")
    public String rechargePackageList(ModelMap map, HttpServletRequest request) {
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<RechargePackage> list = rechargePackageMapper.selectAllPackage();
        PageInfo pageInfo = new PageInfo(list);
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }

        map.addAttribute("list", pageInfo.getList());
        map.addAttribute("pageNum", pageInfo.getPageNum());
        map.addAttribute("nextPage", pageInfo.getNextPage());
        map.addAttribute("prePage", pageInfo.getPrePage());
        map.addAttribute("numbers", numbers);
        map.put("date", new DateTool());
        return "rechargePackageList";
    }

    @RequestMapping("/getRechargePackage")
    public String getRechargePackage(ModelMap map, HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id"));
        RechargePackage p = rechargePackageMapper.selectById(id);
        map.addAttribute("package", p);
        map.put("date", new DateTool());
        return "rechargePackage";
    }

    @RequestMapping("/addRechargePackageUI")
    public String addRechargePackageUI(ModelMap map, HttpServletRequest request) {
        return "addRechargePackage";
    }

    @RequestMapping("/addRechargePackage")
    @ResponseBody
    public Results addRechargePackage(HttpServletRequest request) {
        RechargePackage p = new RechargePackage();

        String packageName = request.getParameter("packageName").toString();
        BigDecimal price = new BigDecimal(request.getParameter("price").toString());
        Long gameMoney = Long.valueOf(request.getParameter("gameMoney").toString());

        p.setPackageName(packageName);
        p.setPrice(price);
        p.setGameMoney(gameMoney);
        p.setStatus(1);
        rechargePackageMapper.save(p);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

    @RequestMapping("/deletePackage")
    public String deletePackage(ModelMap map, HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id").toString());
        rechargePackageMapper.deleteById(id);
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<RechargePackage> list = rechargePackageMapper.selectAllPackage();
        PageInfo pageInfo = new PageInfo(list);
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }

        map.addAttribute("list", pageInfo.getList());
        map.addAttribute("pageNum", pageInfo.getPageNum());
        map.addAttribute("nextPage", pageInfo.getNextPage());
        map.addAttribute("prePage", pageInfo.getPrePage());
        map.addAttribute("numbers", numbers);
        map.put("date", new DateTool());
        return "rechargePackageList";
    }

    @RequestMapping("/rechargeOrderList")
    public String rechargeOrderList(ModelMap map, HttpServletRequest request) {

        Long userId = request.getParameter("userId") == null || request.getParameter("userId").toString().equals("") ? null : Long.valueOf(request.getParameter("userId"));
        String id = request.getParameter("id") == null ? null : request.getParameter("id");

        Map<String, Object> params = new HashedMap();
        if (userId != null && userId > 0) {
            params.put("userId", userId);
        }
        if (id != null && !id.equals("")) {
            params.put("id", id);
        }

        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }

        PageHelper.startPage(Integer.valueOf(page), 10);
        List<RechargeOrder> list = rechargeOrderMapper.selectAllRechargeOrder(params);
        PageInfo pageInfo = new PageInfo(list);
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }

        map.addAttribute("list", pageInfo.getList());
        map.addAttribute("pageNum", pageInfo.getPageNum());
        map.addAttribute("nextPage", pageInfo.getNextPage());
        map.addAttribute("prePage", pageInfo.getPrePage());
        map.addAttribute("numbers", numbers);
        map.put("date", new DateTool());
        return "rechargeOrderList";
    }

    @RequestMapping("/getRechargeOrder")
    public String getRechargeOrder(ModelMap map, HttpServletRequest request) {
        String id = request.getParameter("id");
        RechargeOrder order = rechargeOrderMapper.selectById(id);
        map.addAttribute("order", order);
        map.put("date", new DateTool());
        return "rechargeOrder";
    }

    @RequestMapping("/expressList")
    public String expressList(ModelMap map, HttpServletRequest request) {
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        int siz = 50;
        Integer status = request.getParameter("status") == null ? null : Integer.valueOf(request.getParameter("status"));
        Map<String, Object> params = new HashMap<>();
        if (status != null && status > 0) {
            params.put("status", status);
            siz = 200;
        }

        PageHelper.startPage(Integer.valueOf(page), siz);
        List<Express> list = expressMapper.selectAll(params);
        PageInfo pageInfo = new PageInfo(list);


        List<OrderExpress> relist = orderService.toExpress(list);
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }
        map.addAttribute("list", relist);
        map.addAttribute("pageNum", pageInfo.getPageNum());
        map.addAttribute("nextPage", pageInfo.getNextPage());
        map.addAttribute("prePage", pageInfo.getPrePage());
        map.addAttribute("numbers", numbers);
        map.put("date", new DateTool());
        return "expressList";
    }

    @RequestMapping("/getExpress")
    public String getExpress(ModelMap map, HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id").toString());
        Express express = expressMapper.selectById(id);

        map.addAttribute("data", express);
        return "expressInfo";
    }

    @RequestMapping("/udpateExpress")
    @ResponseBody
    public Results udpateExpress(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id").toString());
        String outOrderId = request.getParameter("outOrderId");
        if (outOrderId != null && !outOrderId.equals("")) {
            Express express = expressMapper.selectById(id);

            express.setOutOrderId(outOrderId);
            express.setStatus(2);
            expressMapper.update(express);
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(express.getOrderId());
            orderInfo.setStatus(5);
            orderInfoMapper.update(orderInfo);
        }
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }


}
