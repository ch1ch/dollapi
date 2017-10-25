package com.dollapi.mapper;

import com.dollapi.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserInfoMapper {


    public void save(UserInfo userInfo);


    public void update(UserInfo userInfo);

    public UserInfo selectByUnionId(String unionId);

    public UserInfo selectByToken(String token);


//    ============================UI===================================

    List<UserInfo> selectAllUser();

    UserInfo selectUserById(Long userId);
}
