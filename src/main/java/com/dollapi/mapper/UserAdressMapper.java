package com.dollapi.mapper;

import com.dollapi.domain.UserAdress;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserAdressMapper {

    public void save(UserAdress userAdress);

    public void update(UserAdress userAdress);

    List<UserAdress> selectByUserId(Long userId);

    UserAdress selectById(Long id);

    public void deleteById(Long id);

}
