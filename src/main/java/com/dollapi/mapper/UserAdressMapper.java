package com.dollapi.mapper;

import com.dollapi.domain.UserAdress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserAdressMapper {

    public void save(UserAdress userAdress);

    public void update(UserAdress userAdress);

    List<UserAdress> selectByUserId(Long userId);

}
