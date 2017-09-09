package com.dollapi.controller;

import com.dollapi.domain.MachineInfo;
import com.dollapi.servie.MachineService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private MachineService machineService;

    @RequestMapping("/getMachineList")
    public Results getMachineList() {
        List<MachineInfo> list = machineService.getMachineInfoList();
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }

}
