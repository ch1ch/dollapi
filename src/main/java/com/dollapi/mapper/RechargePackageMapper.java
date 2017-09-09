package com.dollapi.mapper;
import com.dollapi.domain.RechargePackage;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface RechargePackageMapper {

	 public void save(RechargePackage rechargePackage);
	

	 public void update(RechargePackage rechargePackage);

}
