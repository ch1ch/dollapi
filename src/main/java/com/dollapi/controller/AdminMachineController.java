package com.dollapi.controller;

import com.dollapi.domain.MachineInfo;
import com.dollapi.mapper.MachineInfoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Copyright: All Rights Reserved</p>
 * <p>Company: 指点无限(北京)科技有限公司   http://www.zhidianwuxian.cn</p>
 * <p>Description:  </p>
 * <p>Author:hexu/方和煦, 2017/9/30</p>
 */

@Controller
@RequestMapping("/admin/machine")
public class AdminMachineController {

    @Autowired
    private MachineInfoMapper machineInfoMapper;

    @RequestMapping("/getMachineList")
    public String getMachineList(ModelMap map, HttpServletRequest request) {
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        PageHelper.startPage(Integer.valueOf(page), 3);
        List<MachineInfo> list = machineInfoMapper.selectAll();
        PageInfo pageInfo = new PageInfo(list);

        List<String> numbers = new ArrayList<>();
        for (int i = 1; i <= pageInfo.getPages(); i++) {
            numbers.add(String.valueOf(i));
        }

        map.addAttribute("list", pageInfo.getList());
        map.addAttribute("pageNum", pageInfo.getPageNum());
        map.addAttribute("nextPage", pageInfo.getNextPage());
        map.addAttribute("prePage", pageInfo.getPrePage());
        map.addAttribute("numbers", numbers);

        return "machineList";
    }

    @RequestMapping("/getMachineById")
    public String getMachineById(ModelMap map, HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id"));
        MachineInfo mac = machineInfoMapper.selectById(id);
        map.addAttribute("mac", mac);
        return "machineInfo";
    }

    @RequestMapping("/updateMachine")
    public String updateMachine(HttpServletRequest request) {

        // FIXME: 2017/10/26 这里不修改图片 另外新建方法修改图片

        Long id = Long.valueOf(request.getParameter("id"));
        String machineName = request.getParameter("machineName");
        String dollName = request.getParameter("dollName");
        String ipAddress = request.getParameter("ipAddress");
        String video1 = request.getParameter("video1");
        String video2 = request.getParameter("video2");
        String video3 = request.getParameter("video3");
        Long gameMoney = Long.valueOf(request.getParameter("gameMoney"));
        MachineInfo mac = new MachineInfo();
        mac.setId(id);
        mac.setDollName(dollName);
        mac.setMachineName(machineName);
        mac.setIpAddress(ipAddress);
        mac.setVideo1(video1);
        mac.setVideo2(video2);
        mac.setVideo3(video3);
        mac.setGameMoney(gameMoney);
        machineInfoMapper.update(mac);
        return "1";
    }


}
