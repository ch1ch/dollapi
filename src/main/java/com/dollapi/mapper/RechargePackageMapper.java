package com.dollapi.mapper;

import com.dollapi.domain.RechargePackage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RechargePackageMapper {

    public void save(RechargePackage rechargePackage);


    public void update(RechargePackage rechargePackage);

    public List<RechargePackage> selectAllPackage();

    public RechargePackage selectById(Long id);

}