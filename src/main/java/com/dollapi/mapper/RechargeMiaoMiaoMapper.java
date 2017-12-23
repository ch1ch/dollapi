package com.dollapi.mapper;

import com.dollapi.domain.RechargeMiaoMiao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RechargeMiaoMiaoMapper {

    @Select("select COUNT(1) from `recharge_miaomiao` where to_days(`create_time`) = to_days(now()) and user_id= #{user_id}")
    Long selectToDayCountByUserId(@Param("user_id") Long user_id);

    @Insert("insert into recharge_miaomiao values(null,#{user_id},NOW())")
    int insert(@Param("user_id") Long user_id);

}
