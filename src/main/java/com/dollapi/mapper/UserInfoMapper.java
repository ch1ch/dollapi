package com.dollapi.mapper;

import com.dollapi.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface UserInfoMapper {


    public void save(UserInfo userInfo);


    public void update(UserInfo userInfo);

    public UserInfo selectByUnionId(String unionId);

    public UserInfo selectByToken(String token);

    public UserInfo selectByCode(String code);


//    ============================UI===================================

    List<UserInfo> selectAllUser(Map<String,Object> params);

    UserInfo selectUserById(Long userId);

    Long selectUserCount();
}
