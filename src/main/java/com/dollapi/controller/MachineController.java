package com.dollapi.controller;

import com.dollapi.domain.MachineInfo;
import com.dollapi.mapper.MachineInfoMapper;
import com.dollapi.service.MachineService;
import com.dollapi.util.ApiContents;
import com.dollapi.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private MachineInfoMapper machineInfoMapper;

    @RequestMapping("/getMachineList")
    public Results getMachineList() {
        List<MachineInfo> list = machineService.getMachineInfoList();
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc(), list);
    }


    @RequestMapping("/updateMachineVideo")
    public Results updateMachineVideo(HttpServletRequest request) {
        Long machineId = request.getParameter("machineId") == null ? null : Long.valueOf(request.getParameter("machineId"));
        String video1 = request.getParameter("video1") == null ? null : request.getParameter("video1").toString();
        String video2 = request.getParameter("video2") == null ? null : request.getParameter("video2").toString();
        MachineInfo info = new MachineInfo();
        info.setId(machineId);
        info.setVideo1(video1);
        info.setVideo2(video2);
        machineInfoMapper.update(info);
        return new Results(ApiContents.NORMAL.value(), ApiContents.NORMAL.desc());
    }

}
