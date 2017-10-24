package com.dollapi.controller;

import com.dollapi.domain.UserInfo;
import com.dollapi.exception.DollException;
import com.dollapi.mapper.UserInfoMapper;
import com.dollapi.util.ApiContents;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {


    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 验证所有参数不为空
     *
     * @param params
     * @return
     */
    public void validParamsNotNull(Object... params) {
        for (Object param : params) {
            if (param == null) {
                throw new DollException(ApiContents.PARAMS_ERROR.value(), ApiContents.PARAMS_ERROR.desc());
            }
        }
    }

    public UserInfo getUserInfo(String token) {
        UserInfo userInfo = userInfoMapper.selectByToken(token);
        if (userInfo == null) {
            throw new DollException(ApiContents.USER_LOGIN_ERROR.value(), ApiContents.USER_LOGIN_ERROR.desc());
        }
        return userInfo;
    }

}
