package com.dollapi.mapper;

import com.dollapi.domain.RechargeOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RechargeOrderMapper {

    public void save(RechargeOrder rechargeOrder);

    public void update(RechargeOrder rechargeOrder);

    public List<RechargeOrder> selectByUserId(Long userId);


}
