package com.ruijie.cpu.controller;

import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import com.ruijie.cpu.service.impl.RfCpuServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/outside")
public class OutSideController {

    @Autowired
    RfCpuServiceImpl rfCpuService;


    @RequestMapping("/getMachineList")
    @ResponseBody
    public List<MachineTask> getMachineList(@RequestBody UserTask userTask){

//        Map<String,List<RfCase>> caseModels = tagAnalyseService.tagAnalysis(userTask);

        List<MachineTask> ans = rfCpuService.getMachineList(userTask);
        return ans;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "test";
    }


}
