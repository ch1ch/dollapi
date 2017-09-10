package com.dollapi.servie;

import com.dollapi.domain.UserAdress;
import com.dollapi.domain.UserInfo;
import com.dollapi.domain.UserThird;
import com.dollapi.exception.DollException;
import com.dollapi.mapper.UserAdressMapper;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.mapper.UserThirdMapper;
import com.dollapi.util.ApiContents;
import com.dollapi.vo.WXUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("userService")
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserThirdMapper userThirdMapper;

    @Autowired
    private UserAdressMapper userAdressMapper;

    @Value("${com.doll.appid}")
    private String appid;

    @Value("${com.doll.secret}")
    private String secret;

    public UserInfo WXLogin(String code) {

        WXUserInfo wxUserInfo = WXApi.getUserInfo(code, appid, secret);
//        WXUserInfo wxUserInfo = new WXUserInfo();
//        wxUserInfo.setUnionId("fanghexu123");
        if (wxUserInfo.getUnionId() == null || wxUserInfo.getUnionId().equals("")) {
            throw new DollException(ApiContents.WX_USER_NULL.value(), ApiContents.WX_USER_NULL.desc());
        }

        UserInfo userInfo = userInfoMapper.selectByUnionId(wxUserInfo.getUnionId());
        if (userInfo == null) {
            //新建用户
            userInfo = new UserInfo();
            userInfo.setNickName(wxUserInfo.getNickName());
            userInfo.setDollCount(0L);
            userInfo.setUserPoint(0L);
            userInfo.setUserLevel(1);
            userInfo.setGameMoney(0L);
            userInfoMapper.save(userInfo);

            UserThird userThird = new UserThird();
            userThird.setUserId(userInfo.getId());
            // FIXME: 2017/9/9 这里使用枚举
            userThird.setThirdType(1);
            userThird.setUnionId(wxUserInfo.getUnionId());
            userThirdMapper.save(userThird);
        }
        getToken(userInfo);

        return userInfo;
    }

    public void addAddress(UserAdress userAdress) {
        userAdressMapper.save(userAdress);
    }

    public List<UserAdress> getUserAddressList(Long userId) {
        List<UserAdress> list = userAdressMapper.selectByUserId(userId);
        return list;
    }


    private void getToken(UserInfo userInfo) {
        if (userInfo.getToken() == null || userInfo.getToken().equals("")) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            userInfo.setToken(uuid);
            userInfoMapper.update(userInfo);
        }

    }

}
