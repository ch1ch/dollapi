package com.dollapi.mapper;

import com.dollapi.domain.MachineInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface MachineInfoMapper {

    public void save(MachineInfo machineInfo);

    public void update(MachineInfo machineInfo);

	public List<MachineInfo> selectAll();

	MachineInfo selectById(Long id);
}
