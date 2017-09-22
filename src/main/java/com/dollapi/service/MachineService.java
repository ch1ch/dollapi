package com.dollapi.service;

import com.dollapi.domain.MachineInfo;
import com.dollapi.mapper.MachineInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("machineService")
public class MachineService {
    private final static Logger logger = LoggerFactory.getLogger(MachineService.class);

    @Autowired
    private MachineInfoMapper machineInfoMapper;

    public List<MachineInfo> getMachineInfoList() {
        List<MachineInfo> list = machineInfoMapper.selectAll();
        return list;
    }
}
