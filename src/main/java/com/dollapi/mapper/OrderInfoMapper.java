package com.dollapi.mapper;

import com.dollapi.domain.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface OrderInfoMapper {

    public void save(OrderInfo orderInfo);


    public void update(OrderInfo orderInfo);

    public OrderInfo selectById(String id);

    public List<OrderInfo> selectByUserId(Map<String, Object> params);

    public List<OrderInfo> selectAllOrder();

    public List<OrderInfo> selectByMachineId(Long machineId);

    public OrderInfo selectOneByMachineId(Long machineId);


}
