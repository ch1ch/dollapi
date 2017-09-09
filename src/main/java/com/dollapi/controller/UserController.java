package com.dollapi.controller;

import com.dollapi.domain.UserAdress;
import com.dollapi.domain.UserInfo;
import com.dollapi.servie.UserService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("/wxLogin")
    public Results wxLogin(HttpServletRequest request) {
        String code = request.getParameter("code");
        validParamsNotNull(code);
        UserInfo userInfo = userService.WXLogin(code);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), userInfo);
    }

    @RequestMapping("/addUserAddress")
    public Results addUserAddress(HttpServletRequest request) {
        String person = request.getParameter("person");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");
        String token = request.getParameter("token");
        validParamsNotNull(person, mobile, address, token);
        UserAdress userAdress = new UserAdress();
        userAdress.setUserId(getUserInfo(token).getId());
        userAdress.setPerson(person);
        userAdress.setMobile(mobile);
        userAdress.setAddress(address);
        userService.addAddress(userAdress);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

    @RequestMapping("/getUserAddress")
    public Results getUserAddress(HttpServletRequest request) {
        String token = request.getParameter("token");
        validParamsNotNull(token);
        List<UserAdress> list = userService.getUserAddressList(getUserInfo(token).getId());
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }


}
