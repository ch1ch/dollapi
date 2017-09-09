package com.dollapi.mapper;

import com.dollapi.domain.RechargeOrder;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface RechargeOrderMapper {

    public void save(RechargeOrder rechargeOrder);

    public void update(RechargeOrder rechargeOrder);


}
