package com.dollapi.mapper;

import com.dollapi.domain.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface OrderInfoMapper {

    public void save(OrderInfo orderInfo);


    public void update(OrderInfo orderInfo);

    public OrderInfo selectById(Long id);

    public List<OrderInfo> selectByUserId(Map<String, Object> params);


}
