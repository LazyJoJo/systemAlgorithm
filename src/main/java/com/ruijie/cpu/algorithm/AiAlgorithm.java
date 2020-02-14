package com.ruijie.cpu.algorithm;


import com.ruijie.cpu.algorithm.factory.Algorithm;
import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("ai")
@Data
public class AiAlgorithm extends Algorithm {

    @Autowired
    SpaceAlgorithm spaceAlgorithm;
    @Autowired
    TimeAlgorithm timeAlgorithm;

    @Override
    public List<MachineTask> calculate() {
        Map<String, List<RfCase>> map = super.tagAnalysis(userTask);
        return aiCreateConfig(userTask,map);
    }

    public List<MachineTask> aiCreateConfig(UserTask userTask, Map<String ,List<RfCase>> map){

        List<Integer> timeModel = new ArrayList<>(Arrays.asList(4,8,16,20));
        List<MachineTask> tryTask;

        List<RfCase> rfCases = map.get("comAns");
        List<RfCase> singleRfCases = map.get("caseAns");
        List<RfCase> caseCopy = new ArrayList<>(rfCases);
        List<RfCase> singleCaseCopy = new ArrayList<>(singleRfCases);

        String valgrindSup = userTask.getValgrindFunc();
        double val= 1;
        if ("1".equals(valgrindSup)){
            val = 2.2;
        }
        int alltime = 0;
        for(int i = 0; i< rfCases.size(); i++){
            alltime+=(int)(rfCases.get(i).getSumTime()*val);
        }

        if (rfCases.size()!=0) {
            Collections.sort(rfCases);
            int maxtime = (int) (rfCases.get(rfCases.size() - 1).getSumTime() * val);
            for (int num : timeModel) {
                if (num * 3600 > maxtime ) {
                    if (alltime>num*3600*10){
                        continue;
                    }
                    userTask.setNeedFinishTime(String.valueOf(num));
                    tryTask = timeAlgorithm.calculate(userTask, map);
                    if (tryTask.size() <= 10) {
                        return tryTask;
                    }else{
                        rfCases = new ArrayList<>(caseCopy);
                        singleRfCases = new ArrayList<>(singleCaseCopy);
                        map.put("comAns", rfCases);
                        map.put("caseAns", singleRfCases);
                    }
                }
            }
            userTask.setNeedFinishTime("");
            userTask.setMachineNum("14");
            tryTask = spaceAlgorithm.calculate(userTask,map);
            return tryTask;
        }else{
            userTask.setMachineNum("1");
            tryTask = spaceAlgorithm.calculate(userTask,map);
            return tryTask;
        }

    }
}
