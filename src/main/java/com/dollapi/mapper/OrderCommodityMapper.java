package com.dollapi.mapper;
import com.dollapi.domain.OrderCommodity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrderCommodityMapper {

	 public void save(OrderCommodity orderCommodity);

	 public void update(OrderCommodity orderCommodity);


}
