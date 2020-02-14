package com.ruijie.cpu.service.impl;

import com.ruijie.cpu.algorithm.factory.Algorithm;
import com.ruijie.cpu.algorithm.factory.AlgorithmFactory;
import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RfCpuServiceImpl {


    @Autowired
    AlgorithmFactory algorithmFactory;

    public List<MachineTask> getMachineList(UserTask userTask){
        if (userTask.getMethodType()==null){
            userTask.setMethodType("ai");
        }
        Algorithm algorithm = algorithmFactory.getBy(userTask.getMethodType());
        algorithm.setUserTask(userTask);
        List<MachineTask> list = algorithm.calculate();

        return list;
    }







}
