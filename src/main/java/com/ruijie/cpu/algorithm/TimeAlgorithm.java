package com.ruijie.cpu.algorithm;


import com.ruijie.cpu.algorithm.factory.Algorithm;
import com.ruijie.cpu.entity.config.ConfigInfo;
import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("time")
@Data
public class TimeAlgorithm extends Algorithm {

    @Override
    public List<MachineTask> calculate() {
        Map<String, List<RfCase>> map = super.tagAnalysis(userTask);
        return timeCreateConfigEffective(userTask,map);
    }

    public List<MachineTask> calculate(UserTask userTask, Map<String ,List<RfCase>> map) {
        return timeCreateConfigEffective(userTask,map);
    }

    /**
     * <p>Description: </p>
     * <p>Create Time: 2019/7/27 </p>
     * @author zhengchengbin
     * @param
     */
    public List<MachineTask> timeCreateConfigEffective(UserTask userTask, Map<String ,List<RfCase>> map){
        int needTime = (int)(Integer.valueOf(userTask.getNeedFinishTime()));
        List<MachineTask> tasks = new ArrayList<>();
        List<ConfigInfo> configList = new ArrayList<>();
        String valgrindSup = userTask.getValgrindFunc();
        double val = 1;
        int needSecond = needTime * 60 * 60;
        if ("1".equals(valgrindSup)){
            val = 2.2;
        }

        List<RfCase> rfCases = map.get("comAns");
        List<RfCase> singleRfCases = map.get("caseAns");


        // new comp or no time
        List<RfCase> noTimeCases = new ArrayList<>();
        for (int i = 0; i < rfCases.size(); i++) {
            if (rfCases.get(i).getSumTime() * val == 0) {
                noTimeCases.add(rfCases.get(i));
            }
        }

        if(noTimeCases.size()!=0){
            ConfigInfo configInfo = super.produceConfig(userTask, noTimeCases, val,false);
            configInfo.setTimeSum(30*60);
            configList.add(configInfo);
            rfCases.removeAll(noTimeCases);
        }
        // almost timed out or time out
        List<RfCase> timeOutCase = new ArrayList<>();
        for(RfCase rfCase : rfCases){
            if(rfCase.getSumTime()*val/needSecond>=0.95){
                List<RfCase> list = new ArrayList<>();
                list.add(rfCase);
                ConfigInfo configInfo = super.produceConfig(userTask, list, val,false);
                configList.add(configInfo);
                timeOutCase.add(rfCase);
            }
        }
        rfCases.removeAll(timeOutCase);


        // normal
        int len = rfCases.size();
        while (len > 0) {
            List<RfCase> removeList = this.calculate2(needSecond, rfCases, val);
            ConfigInfo configInfo = super.produceConfig(userTask, removeList, val,false);
            configList.add(configInfo);
            rfCases.removeAll(removeList);
            len -= removeList.size();
        }


        if(singleRfCases.size()!=0){
            ConfigInfo configInfo = super.produceConfig(userTask, singleRfCases, val,true);
            configList.add(configInfo);
        }


        for (int i=0;i<configList.size();i++){
            MachineTask machineTask = super.createMachineTask(configList.get(i),userTask);
            tasks.add(machineTask);
        }
        return tasks;
    }

    public List<MachineTask> timeCreateConfig(UserTask userTask, Map<String ,List<RfCase>> map){
        int needTime = (int)(Integer.valueOf(userTask.getNeedFinishTime()));
        List<MachineTask> tasks = new ArrayList<>();
        List<ConfigInfo> configList = new ArrayList<>();
        String valgrindSup = userTask.getValgrindFunc();
        double val = 1;
        int needSecond = needTime * 60 * 60;
        if ("1".equals(valgrindSup)){
            val = 2.2;
        }

        List<RfCase> rfCases = map.get("comAns");
        List<RfCase> singleRfCases = map.get("caseAns");


        for (int i = 0; i < rfCases.size(); i++) {
            double num = Math.ceil(rfCases.get(i).getSumTime() * val);
            if (num > needSecond) {
                needSecond =  (int)num;
            }
        }

        List<RfCase> beyongList = new ArrayList<>();
        for (int i = 0; i < rfCases.size(); i++) {
            if (rfCases.get(i).getSumTime() * val == 0) {
                beyongList.add(rfCases.get(i));
            }
        }
        rfCases.removeAll(beyongList);

        List<RfCase> singleMachineRun = new ArrayList<>();
        for(RfCase rfCase : rfCases){
            if(rfCase.getSumTime()*val/needSecond>=0.9){
                List<RfCase> list = new ArrayList<>();
                list.add(rfCase);
                ConfigInfo configInfo = super.produceConfig(userTask, list, val,false);
                configList.add(configInfo);
                singleMachineRun.add(rfCase);
            }
        }
        rfCases.removeAll(singleMachineRun);


        int len = rfCases.size();
        while (len > 0) {
            List<RfCase> removeList = calculate2(needSecond, rfCases, val);
            ConfigInfo configInfo = super.produceConfig(userTask, removeList, val,false);
            configList.add(configInfo);
            rfCases.removeAll(removeList);
            len -= removeList.size();
        }


        if(singleRfCases.size()!=0){
            ConfigInfo configInfo = super.produceConfig(userTask, singleRfCases, val,true);
            configList.add(configInfo);
        }

        if(beyongList.size()!=0){
            ConfigInfo configInfo = super.produceConfig(userTask, beyongList, val,false);
            configInfo.setTimeSum(30*60);
            configList.add(configInfo);
        }

        for (int i=0;i<configList.size();i++){
            MachineTask machineTask = super.createMachineTask(configList.get(i),userTask);
            tasks.add(machineTask);
        }
        return tasks;
    }


    // 目前是不用的
    public List<RfCase> calculate3(int needTime, List<RfCase> rfCases, double val){
        int addTime = 3*60;
        needTime +=addTime;
        int[][] v = new int[rfCases.size()+1][needTime+1];
        String[][] comps = new String[rfCases.size()+1][needTime+1];
        for (int i=0;i<needTime+1;i++){
            v[0][i] = 0;
            comps[0][i] = "";
        }
        for(int i = 0; i< rfCases.size()+1; i++){
            for(int j=0;j<addTime+1;j++){
                v[i][j] = 0;
                comps[i][j] = "";
            }
        }

        for (int i = 1; i< rfCases.size()+1; i++){
            for (int j=addTime;j<needTime+1;j++){
                int node_v = (int)(rfCases.get(i-1).getSumTime()*val);
                if (node_v +addTime> j){
                    v[i][j] = v[i-1][j];  // 继承当前最大值
                    comps[i][j] = comps[i-1][j];
                }else{  //比较当前最大值和加上本节点值的结果
                    if(comps[i-1][j-node_v].indexOf(rfCases.get(i-1).getComp())!=-1){
                        if(v[i-1][j-node_v]+node_v>=v[i-1][j]){
                            v[i][j] = v[i-1][j-node_v]+node_v;
                            comps[i][j] = comps[i-1][j-node_v];
                        }else{
                            v[i][j] = v[i-1][j];
                            comps[i][j] = comps[i-1][j];
                        }
                    }else{
                        // 可能产生null值，当组件的运行时间小于addtime时，
                        if(v[i-1][j-node_v-addTime]+node_v+addTime>v[i-1][j]){
                            v[i][j] = v[i-1][j-node_v-addTime]+node_v+addTime;
                            comps[i][j] = comps[i-1][j-node_v-addTime];
                            comps[i][j]+=(","+rfCases.get(i-1).getComp());
                        }else{
                            v[i][j] = v[i-1][j];
                            comps[i][j] = comps[i-1][j];
                        }
                    }
                }
            }
        }

        List<RfCase> ans = new ArrayList<>();
        for (int j = needTime, i = rfCases.size(); i>0; i--){
            if (v[i][j]!=v[i-1][j]){
                ans.add(rfCases.get(i-1));
                if(comps[i][j].equals(comps[i-1][j])){
                    j = j-(int)(rfCases.get(i-1).getSumTime()*val);
                }else{
                    j = j-(int)(rfCases.get(i-1).getSumTime()*val)-addTime;
                }

            }
        }
        return ans;
    }

    // 目前已经支持相同组件包的放在一起
    public List<RfCase> calculate2(int needTime, List<RfCase> rfCases, double val){
        int addTime = 3*60;
        int[][] v = new int[rfCases.size()+1][needTime+1];
        String[][] comps = new String[rfCases.size()+1][needTime+1];
        for (int i=0;i<needTime+1;i++){
            v[0][i] = -1000;
            comps[0][i] = "";
        }
        for(int i = 0; i< rfCases.size()+1; i++){
            v[i][0] = -1000;
            comps[i][0] = "";
        }

        for (int i = 1; i< rfCases.size()+1; i++){
            for (int j=1; j<needTime+1;j++){
                int packNum = (int)(rfCases.get(i-1).getSumTime()*val);
                if (packNum > j){
                    v[i][j] = v[i-1][j];  // inherit current maximum
                    comps[i][j] = comps[i-1][j];
                }else{  // add rewards and punishment system
                    if(comps[i-1][j].indexOf(rfCases.get(i-1).getComp())!=-1){
                        int node_v = packNum;
                        if(v[i-1][j-packNum]+node_v>=v[i-1][j]){
                            v[i][j] = v[i-1][j-packNum]+node_v;
                            comps[i][j] = comps[i-1][j-packNum];
                        }else{
                            v[i][j] = v[i-1][j];
                            comps[i][j] = comps[i-1][j];
                        }
                    }else{
                        int node_v = packNum-addTime;
                        if (node_v <=0){
                            node_v = 1;
                        }
                        if(v[i-1][j-packNum]+node_v>v[i-1][j]){
                            v[i][j] = v[i-1][j-packNum]+node_v;
                            comps[i][j] = comps[i-1][j-packNum];
                            comps[i][j]+=(","+rfCases.get(i-1).getComp());
                        }else{
                            v[i][j] = v[i-1][j];
                            comps[i][j] = comps[i-1][j];
                        }
                    }
                }
            }
        }

        List<RfCase> ans = new ArrayList<>();
        for (int j = needTime, i = rfCases.size(); i>0; i--){
            if (v[i][j]!=v[i-1][j]){
                ans.add(rfCases.get(i-1));
                j = j-(int)(rfCases.get(i-1).getSumTime()*val);
            }
        }
        return ans;
    }


    /**
     * <p>Description: 最早版本 n*m </p>
     * <p>Create Time: 2019/7/29 </p>
     * @author zhengchengbin
     * @param
     * @version 1.0
     */
    public List<RfCase> calculate(int needTime, List<RfCase> rfCases, double val){
        int[][] v = new int[rfCases.size()+1][needTime+1];
        for (int i=0;i<needTime+1;i++){
            v[0][i] = 0;
        }
        for(int i = 0; i< rfCases.size()+1; i++){
            v[i][0] = 0;
        }

        for (int i = 1; i< rfCases.size()+1; i++){
            for (int j=1;j<needTime+1;j++){
                int node_v = (int)(rfCases.get(i-1).getSumTime()*val);
                if (node_v > j){
                    v[i][j] = v[i-1][j];  // 继承当前最大值
                }else{
                    v[i][j] = Math.max(v[i-1][j-node_v]+node_v, v[i-1][j]); //比较当前最大值和加上本节点值的结果
                }
            }
        }
        List<RfCase> ans = new ArrayList<>();
        for (int j = needTime, i = rfCases.size(); i>0; i--){
            if (v[i][j]!=v[i-1][j]){
                ans.add(rfCases.get(i-1));
                j = j-(int)(rfCases.get(i-1).getSumTime()*val);
            }
        }
        return ans;
    }
}
