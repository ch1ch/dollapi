package com.dollapi.mapper;

import com.dollapi.domain.RechargeOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RechargeOrderMapper {

    public void save(RechargeOrder rechargeOrder);

    public void update(RechargeOrder rechargeOrder);

    public List<RechargeOrder> selectByUserId(Long userId);

    public RechargeOrder selectById(String id);

    public List<RechargeOrder> selectAllRechargeOrder();


}
