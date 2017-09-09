package com.dollapi.mapper;

import com.dollapi.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserInfoMapper {


    public void save(UserInfo userInfo);


    public void update(UserInfo userInfo);

    public UserInfo selectByUnionId(String unionId);

    public UserInfo selectByToken(String token);

}
