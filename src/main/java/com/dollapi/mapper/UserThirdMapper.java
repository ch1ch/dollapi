package com.dollapi.mapper;

import com.dollapi.domain.UserThird;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserThirdMapper {

    UserThird selectByUnionId(String unionId);

    void save(UserThird userThird);

}
