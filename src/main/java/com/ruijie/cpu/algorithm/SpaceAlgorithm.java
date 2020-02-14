package com.ruijie.cpu.algorithm;



import lombok.Data;
import org.springframework.stereotype.Component;

import com.ruijie.cpu.algorithm.factory.Algorithm;
import com.ruijie.cpu.entity.config.ConfigInfo;
import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("space")
@Data
public class SpaceAlgorithm extends Algorithm {


    @Override
    public List<MachineTask> calculate() {
        Map<String, List<RfCase>> map = super.tagAnalysis(userTask);
        return spaceCreateConfig(userTask,map);
    }

    public List<MachineTask> calculate(UserTask userTask, Map<String ,List<RfCase>> map) {
        return spaceCreateConfig(userTask,map);
    }

    public List<MachineTask> spaceCreateConfig(UserTask userTask, Map<String ,List<RfCase>> map){
        List<MachineTask> tasks = new ArrayList<>();
        String valgrindSup = userTask.getValgrindFunc();
        double val= 1;
        if ("1".equals(valgrindSup)){
            val = 2.2;
        }

        List<RfCase> rfCases = map.get("comAns");   // 用例套的
        List<RfCase> singleRfCases = map.get("caseAns");  //单用例标签的

        List<RfCase> beyongList = new ArrayList<>();
        if (rfCases.size()!=0){
            for (int i = 0; i < rfCases.size(); i++) {
                if (rfCases.get(i).getSumTime() * val == 0) {
                    beyongList.add(rfCases.get(i));
                }
            }
            rfCases.removeAll(beyongList);  // 新用例
        }

        Collections.sort(rfCases);

        int needNum = Integer.valueOf(userTask.getMachineNum());
        List<ConfigInfo> configList = this.produceBaseConfig(userTask,needNum); //袋子

        for(int i = rfCases.size()-1; i>=0; i--){
            RfCase rfCase = rfCases.get(i);
            Collections.sort(configList);
            ConfigInfo smallBug = configList.get(0);
            int count = smallBug.getCaseCount();
            count+= rfCase.getCount();
            smallBug.setCaseCount(count);
            int time = smallBug.getTimeSum();
            time+=(int)(rfCase.getSumTime()*val);
            smallBug.setTimeSum(time);
            String tag = smallBug.getComponentTags();
            if (tag==null){
                tag = "";
            }
            smallBug.setComponentTags(tag+ rfCase.getComp()+"."+ rfCase.getCom()+",");
        }

        // 在新机子中运行
        if(singleRfCases.size()>0){
            ConfigInfo configInfo = super.produceConfig(userTask,singleRfCases,val,true);
            configList.add(configInfo);
        }

        if (beyongList.size()>0){
            // 在新机子中运行
            ConfigInfo configInfo = super.produceConfig(userTask,beyongList,val,false);
            configInfo.setTimeSum(30*60);   // 时间未知，统一设为半个小时吧
            configList.add(configInfo);
        }


        for(int i=0;i<configList.size();i++){
            ConfigInfo configInfo = configList.get(i);
            String comtag = configInfo.getComponentTags();
            String tag = configInfo.getSingleCaseTag();
            if (comtag!=null&&!"".equals(comtag)){
                configInfo.setComponentTags(comtag.substring(0,comtag.length()-1));
            }
            if(tag!=null&&!"".equals(tag)){
                configInfo.setSingleCaseTag(tag.substring(0,tag.length()-1));
            }
        }

        for (int i=0;i<configList.size();i++){
            if (configList.get(i).getCaseCount()==0){
                continue;
            }
            MachineTask machineTask = super.createMachineTask(configList.get(i),userTask);
            tasks.add(machineTask);
        }

        return tasks;
    }


    public List<ConfigInfo> produceBaseConfig(UserTask userTask, int num){
        List<ConfigInfo> list = new ArrayList<>(num);
        for (int i=0;i<num;i++){
            ConfigInfo configInfo = new ConfigInfo(callIp);
            configInfo = super.produceBaseConfig(userTask,configInfo);
            list.add(configInfo);
        }
        return list;
    }


}
