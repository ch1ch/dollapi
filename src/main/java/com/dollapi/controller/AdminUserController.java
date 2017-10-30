package com.dollapi.controller;

import com.dollapi.domain.UserInfo;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>Copyright: All Rights Reserved</p>
 * <p>Company: 指点无限(北京)科技有限公司   http://www.zhidianwuxian.cn</p>
 * <p>Description:  </p>
 * <p>Author:hexu/方和煦, 2017/9/22</p>
 */

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @RequestMapping("/userList")
    public String userList(ModelMap map, HttpServletRequest request) {
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 10);
        List<UserInfo> list = userInfoMapper.selectAllUser();
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

        return "userList";
    }

    @RequestMapping("/getUserInfo")
    public String getUserInfo(ModelMap map, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getParameter("userId"));
        UserInfo user = userInfoMapper.selectUserById(userId);
        map.addAttribute("user", user);
        map.put("date", new DateTool());
        return "userInfo";
    }

    @RequestMapping("/updateUserInfo")
    @ResponseBody
    public Results updateUserInfo(HttpServletRequest request) {
        UserInfo user = new UserInfo();
        user.setId(Long.valueOf(request.getParameter("id")));
        user.setNickName(request.getParameter("nickName").toString());
        user.setUserLevel(Integer.valueOf(request.getParameter("userLevel")));
        user.setPhoneNumber(request.getParameter("phoneNumber"));
        user.setUserPoint(Long.valueOf(request.getParameter("userPoint")));
        user.setGameMoney(Long.valueOf(request.getParameter("gameMoney")));
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        try {
//            user.setCreateTime(sdf.parse(request.getParameter("createTime")));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        userInfoMapper.update(user);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

}
