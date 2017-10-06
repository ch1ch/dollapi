package com.dollapi.controller;

import com.dollapi.domain.UserAdress;
import com.dollapi.domain.UserInfo;
import com.dollapi.service.UserService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import com.dollapi.util.TLSUtil;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Value("${tls.sdkappid}")
    private Long sdkAppId;

    @Value("${tls.publickey}")
    private String publickey;

    @Value("${tls.privatekey}")
    private String privatekey;

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

    @RequestMapping("/getLTSSig")
    public Results getLTSSig(HttpServletRequest request) {
        String token = request.getParameter("token");
        validParamsNotNull(token);
        String sig = TLSUtil.getSig("user" + getUserInfo(token).getId().toString(), privatekey, publickey, sdkAppId);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), sig);
    }

    @RequestMapping("/getUserInfo")
    public Results getUserInfo(HttpServletRequest request) {
        String token = request.getParameter("token");
        validParamsNotNull(token);
        UserInfo user = getUserInfo(token);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), user);
    }

    @RequestMapping("/test")
    public Results test(HttpServletRequest request) {
        String a=request.getParameter("a");
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd2620361786fgzrcs.wilddogio.com").build();
        WilddogApp.initializeApp(options);
        SyncReference ref = WilddogSync.getInstance().getReference();
        HashMap<String, Object> user = new HashMap<>();
        user.put("1",15);
        user.put("2",16);
        ref.child("2").setValue(user);


//        ref.child("Jobs").setValue(user, new SyncReference.CompletionListener() {
//            @Override
//            public void onComplete(SyncError error, SyncReference ref) {
//                if (error != null) {
//                    System.out.println(error.getMessage());
//                } else {
//                    System.out.println("setValue success");
//                }
//            }
//        });
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());

    }


}
