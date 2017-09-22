package com.dollapi.controller;

import com.dollapi.domain.UserInfo;
import com.dollapi.mapper.UserInfoMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
    public String userList(ModelMap map) {
        map.addAttribute("test", "哈哈哈哈哈");

        PageHelper.startPage(1, 3);
        List<UserInfo> list=userInfoMapper.selectAllUser();
        PageInfo pageInfo = new PageInfo(list);
        map.addAttribute("list", pageInfo.getList());
        return "userList";
    }

}
