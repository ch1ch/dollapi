package com.dollapi.controller;

import com.dollapi.domain.OrderInfo;
import com.dollapi.mapper.OrderInfoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping("/orderList")
    public String orderList(ModelMap map, HttpServletRequest request) {
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<OrderInfo> list = orderInfoMapper.selectAllOrder();
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

}
