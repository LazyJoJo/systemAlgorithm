package com.ruijie.cpu.service;

import com.ruijie.cpu.entity.ConfigInfo;
import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import com.ruijie.cpu.utils.EqualUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RfCpuServiceImpl {

    @Value("${cas.appServerUrl}")
    String callIp;

    /**
     * <p>Description: 生成配置文件</p>
     * <p>Create Time: 2019/7/24 </p>
     * @author zhengchengbin
     * @param
     */
    public List<MachineTask> createConfig(UserTask userTask, Map<String ,List<RfCase>> map){

        // use effective way
        if ("time".equals(userTask.getMethodType())){
            return timeCreateConfigEffective(userTask,map);
        }
        if("space".equals(userTask.getMethodType())){
            return spaceCreateConfig(userTask,map);
        }
        // use the most advantageous way for the system
        if("ai".equals(userTask.getMethodType())){
            return aiCreateConfig(userTask,map);
        }
        return null;
    }


    public List<MachineTask> timeCreateConfig(UserTask userTask, Map<String ,List<RfCase>> map){
        int needTime = (int)(Integer.valueOf(userTask.getNeedFinishTime()));  //取消缓冲时间
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

        // 最大运行时间原则，保证机子数量最小
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
        rfCases.removeAll(beyongList);  //删除没有用例的单体

        List<RfCase> singleMachineRun = new ArrayList<>();
        for(RfCase rfCase : rfCases){
            if(rfCase.getSumTime()*val/needSecond>=0.9){
                List<RfCase> list = new ArrayList<>();
                list.add(rfCase);
                ConfigInfo configInfo = produceConfig(userTask, list, val,false);
                configList.add(configInfo);
                singleMachineRun.add(rfCase);
            }
        }
        rfCases.removeAll(singleMachineRun);


        int len = rfCases.size();
        while (len > 0) {  //多个单体一机运行
            List<RfCase> removeList = calculate2(needSecond, rfCases, val);
            ConfigInfo configInfo = produceConfig(userTask, removeList, val,false);
            configList.add(configInfo);
            rfCases.removeAll(removeList);
            len -= removeList.size();
        }

        // todo 用例数不会太多，先设成这样处理吧
        // 在新机子中运行
        if(singleRfCases.size()!=0){
            ConfigInfo configInfo = produceConfig(userTask, singleRfCases, val,true);
            configList.add(configInfo);
        }

        // 新组件级用例，在新机中运行（只能区分组件级用例，无法做到用例级）
        if(beyongList.size()!=0){
            ConfigInfo configInfo = produceConfig(userTask, beyongList, val,false);
            configInfo.setTimeSum(30*60);   //时间未知，统一设为半个小时吧
            configList.add(configInfo);
        }

        for (int i=0;i<configList.size();i++){
            MachineTask machineTask = createMachineTask(configList.get(i),userTask);
            tasks.add(machineTask);
        }
        return tasks;
    }

    /**
     * <p>Description: </p>
     * <p>Create Time: 2019/7/27 </p>
     * @author zhengchengbin
     * @param
     */



    public List<MachineTask> timeCreateConfigEffective(UserTask userTask, Map<String ,List<RfCase>> map){
        int needTime = (int)(Integer.valueOf(userTask.getNeedFinishTime()));  //取消缓冲时间
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
            ConfigInfo configInfo = produceConfig(userTask, noTimeCases, val,false);
            configInfo.setTimeSum(30*60);   //时间未知，统一设为半个小时吧
            configList.add(configInfo);
            rfCases.removeAll(noTimeCases);
        }
        // almost timed out or time out
        List<RfCase> timeOutCase = new ArrayList<>();
        for(RfCase rfCase : rfCases){
            if(rfCase.getSumTime()*val/needSecond>=0.95){
                List<RfCase> list = new ArrayList<>();
                list.add(rfCase);
                ConfigInfo configInfo = produceConfig(userTask, list, val,false);
                configList.add(configInfo);
                timeOutCase.add(rfCase);
            }
        }
        rfCases.removeAll(timeOutCase);


        // normal
        int len = rfCases.size();
        while (len > 0) {
            List<RfCase> removeList = calculate2(needSecond, rfCases, val);
            ConfigInfo configInfo = produceConfig(userTask, removeList, val,false);
            configList.add(configInfo);
            rfCases.removeAll(removeList);
            len -= removeList.size();
        }

        // todo 用例数不会太多，先设成这样处理吧
        // 在新机子中运行
        if(singleRfCases.size()!=0){
            ConfigInfo configInfo = produceConfig(userTask, singleRfCases, val,true);
            configList.add(configInfo);
        }


        for (int i=0;i<configList.size();i++){
            MachineTask machineTask = createMachineTask(configList.get(i),userTask);
            tasks.add(machineTask);
        }
        return tasks;
    }

    public List<MachineTask> aiCreateConfig(UserTask userTask, Map<String ,List<RfCase>> map){

        List<Integer> timeModel = new ArrayList<>(Arrays.asList(4,8,16,20));
        List<MachineTask> tryTask;
        List<RfCase> rfCases = map.get("comAns");   // 用例套的
        List<RfCase> singleRfCases = map.get("caseAns");  //单用例标签的
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
                    tryTask = timeCreateConfig(userTask, map);
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
            tryTask = spaceCreateConfig(userTask,map);
            return tryTask;
        }else{
            userTask.setMachineNum("1");
            tryTask = spaceCreateConfig(userTask,map);
            return tryTask;
        }

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
        List<ConfigInfo> configList = produceBaseConfig(userTask,needNum); //袋子

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
            ConfigInfo configInfo = produceConfig(userTask,singleRfCases,val,true);
            configList.add(configInfo);
        }

        if (beyongList.size()>0){
            // 在新机子中运行
            ConfigInfo configInfo = produceConfig(userTask,beyongList,val,false);
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
            MachineTask machineTask = createMachineTask(configList.get(i),userTask);
            tasks.add(machineTask);
        }

        return tasks;
    }

    public ConfigInfo produceConfig(UserTask userTask, List<RfCase> list, double val, boolean single){
        int sum = 0;
        int count = 0;
        String tag = "";
        String singleTag = "";

        for (int i=0;i<list.size();i++){
            if (single){
                singleTag += list.get(i).getTag() + ",";
                sum += list.get(i).getLongTime();
                count += 1;
            }else{
                tag+=list.get(i).getComp()+"."+list.get(i).getCom()+",";
                sum+=(int)(list.get(i).getSumTime()*val);
                count+=list.get(i).getCount();
            }
        }
        ConfigInfo configInfo = new ConfigInfo(callIp);
        configInfo.setTopLevel(userTask.getCaseGrade());
        if(!EqualUtil.isNull(tag)){
            configInfo.setComponentTags(tag.substring(0, tag.length()-1));
        }
        if(!EqualUtil.isNull(singleTag)){
            configInfo.setSingleCaseTag(singleTag.substring(0, singleTag.length()-1));
        }
        configInfo.setNormalRfMail(userTask.getNormalRfMail());
        configInfo.setValgrindMail(userTask.getValgrindMail());
        configInfo.setValgrindFunc(userTask.getValgrindFunc());
        configInfo.setProductTags(userTask.getProject());
        configInfo.setExcludeTags(userTask.getExcludeTags());  // 基本弃用
        configInfo.setTimeSum(sum);
        configInfo.setCaseCount(count);
        return configInfo;
    }

    public List<ConfigInfo> produceBaseConfig(UserTask userTask,int num){
        List<ConfigInfo> list = new ArrayList<>(num);
        for (int i=0;i<num;i++){
            ConfigInfo configInfo = new ConfigInfo(callIp);
            configInfo.setTopLevel(userTask.getCaseGrade());
            configInfo.setNormalRfMail(userTask.getNormalRfMail());
            configInfo.setValgrindMail(userTask.getValgrindMail());
            configInfo.setValgrindFunc(userTask.getValgrindFunc());
            configInfo.setProductTags(userTask.getProject());
            configInfo.setExcludeTags(userTask.getExcludeTags());  // 基本弃用
            list.add(configInfo);
        }
        return list;
    }

    public MachineTask createMachineTask(ConfigInfo configInfo,UserTask userTask){
        MachineTask machineTask = new MachineTask();
        machineTask.setConfig(configInfo.toString());
        machineTask.setCaseCount(configInfo.getCaseCount());
        machineTask.setExpectTime(configInfo.getTimeSum());
        machineTask.setComponentTags(configInfo.getComponentTags());
        machineTask.setSingleCaseTag(configInfo.getSingleCaseTag());

        machineTask.setUserTask(userTask);
        machineTask.setExcludeTags(userTask.getExcludeTags());
        machineTask.setTopLevel(userTask.getCaseGrade());
        machineTask.setProductTags(userTask.getProductTags());
        machineTask.setProject(userTask.getProject());
        machineTask.setTaskType(userTask.getTaskType());
        machineTask.setRetentionTime(userTask.getRetentionTime());
        machineTask.setMachineType(userTask.getMachineType());
        return machineTask;
    }

    /**
     * <p>Description: 算法新版本，支持同个组件包尽量放在一起</p>
     * <p>Create Time: 2019/11/7 </p>
     * @author zhengchengbin
     * @param
     */
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



}
