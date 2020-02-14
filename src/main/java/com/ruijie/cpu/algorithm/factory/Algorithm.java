package com.ruijie.cpu.algorithm.factory;


import com.ruijie.cpu.dao.RfCaseDao;
import com.ruijie.cpu.entity.config.ConfigInfo;
import com.ruijie.cpu.entity.MachineTask;
import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import com.ruijie.cpu.entity.mysqlEntity.CaseGroupAns;
import com.ruijie.cpu.utils.EqualUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Data
@Component
public class Algorithm implements TagAnalyseService {

    @Resource
    public RfCaseDao rfCaseDao;

    @Value("${bg.call}")
    public String callIp;

    public UserTask userTask;

    @Override
    public Map<String, List<RfCase>> tagAnalysis(UserTask userTask){

        String comTag = userTask.getCaseTag();   // 组件级别
        String caseTag = userTask.getSingleCaseTags();  // 用例级别
        String level = userTask.getCaseGrade();   //
        String type = userTask.getMachineType();
        List<CaseGroupAns> caseGroup;
        if (type.indexOf("x86")!=-1){
            caseGroup = rfCaseDao.findAllByGroupSupportX86();   //分组时间
        }else{
            caseGroup = rfCaseDao.findAllByGroup();   //分组时间
        }
//        String extag = userTask.getExcludeTags();  // x86_not_support  添加

        List<RfCase> comAns = new ArrayList<>();
        List<RfCase> caseAns = new ArrayList<>();

        if (!EqualUtil.isNull(comTag)) {
            String[] tagAry ;
            comTag = comTag.toLowerCase().trim();
            if ("all".equals(comTag)) {
                comTag = "app,ucast,ip,mpls,security,ras,dev,net,lsm,utils,oam,dc,bridge,mcast,ef,mng,vpn,sslvpn";
            }
            tagAry = comTag.split(",");
            for (int i = 0; i < tagAry.length; i++) {
                String com = "";
                String comp = "";
                String compCom = tagAry[i].trim();
                if (compCom.indexOf(".") != -1) {
                    comp = compCom.split("\\.")[0];
                    com = compCom.split("\\.")[1];
                    List<CaseGroupAns> list = new ArrayList<>();
                    for (CaseGroupAns ans : caseGroup){
                        String anscomp = ans.getComp().toLowerCase();
                        String anscom = ans.getCom().toLowerCase();
                        if (comp.equals(anscomp)&&com.equals(anscom)){
                            list.add(ans);
                        }
                    }
                    List<RfCase> rfCases = getCaseTime( list, level);
                    comAns.addAll(rfCases);
                } else {
                    comp = compCom;
                    List<CaseGroupAns> list = new ArrayList<>();
                    for (CaseGroupAns ans : caseGroup){
                        String anscomp = ans.getComp().toLowerCase();
                        if (comp.equals(anscomp)){
                            list.add(ans);
                        }
                    }
                    List<RfCase> rfCases = getCaseTime(list, level);
                    comAns.addAll(rfCases);
                }
            }
        }

        if (!EqualUtil.isNull(caseTag)) {
            caseTag = caseTag.toUpperCase().trim();
            String[] caseTagAry = caseTag.split(",");

            HashSet<RfCase> rfCaseSet = new HashSet<>();
            for(int i=0;i<caseTagAry.length;i++){
                List<RfCase> rfCases = rfCaseDao.findByTag(caseTagAry[i]);
                if (rfCases.size()==0){
                    RfCase rfCase = new RfCase();
                    rfCase.setTag(caseTagAry[i]);
                    rfCase.setLongTime(2*60);
                    rfCases.add(rfCase);
                }
                rfCaseSet.addAll(rfCases);
            }
            System.out.println("end");
            caseAns.addAll(rfCaseSet);
        }

        Map<String,List<RfCase>> ans = new HashMap<>();
        ans.put("comAns",comAns);
        ans.put("caseAns",caseAns);

        return ans;
    }

    @Override
    public List<RfCase> getCaseTime(List<CaseGroupAns> ans, String level){
        Map<String, RfCase> map = new HashMap<>();
        level = level.toLowerCase();

        for(int i=0;i<ans.size();i++){
            if(level.indexOf("level-min")!=-1&&ans.get(i).getLevel_min()){
                MergeCase(map,ans.get(i));
                continue;
            }
            if(level.indexOf("level0")!=-1&&ans.get(i).getLevel0()){
                MergeCase(map,ans.get(i));
                continue;
            }
            if(level.indexOf("level1")!=-1&&ans.get(i).getLevel1()){
                MergeCase(map,ans.get(i));
                continue;
            }
            if(level.indexOf("level2")!=-1&&ans.get(i).getLevel2()){
                MergeCase(map,ans.get(i));
                continue;
            }
            if(level.indexOf("level3")!=-1&&ans.get(i).getLevel3()){
                MergeCase(map,ans.get(i));
                continue;
            }
            if(level.indexOf("spec")!=-1&&ans.get(i).getSpec()){
                MergeCase(map,ans.get(i));
                continue;
            }
            if(level.indexOf("srs")!=-1&&ans.get(i).getSrs()){
                MergeCase(map,ans.get(i));
                continue;
            }
        }
        List<RfCase> rfCases = new ArrayList<>();
        for(RfCase rfCase :map.values()){
            rfCases.add(rfCase);
        }
        return rfCases;
    }

    @Override
    public void MergeCase(Map<String, RfCase> map, CaseGroupAns ans){
        String compCom = ans.getComp().toLowerCase()+"."+ans.getCom().toLowerCase();
        if (map.get(compCom)!=null){
            RfCase rfCase = map.get(compCom);
            rfCase.setCount(rfCase.getCount()+ans.getRowCount());
            rfCase.setSumTime(rfCase.getSumTime()+ans.getSumTime());
            map.put(compCom, rfCase);
        }else{
            RfCase rfCase = new RfCase();
            rfCase.setCount(ans.getRowCount());
            rfCase.setSumTime(ans.getSumTime());
            rfCase.setComp(ans.getComp());
            rfCase.setCom(ans.getCom());
            map.put(compCom, rfCase);
        }
    }

    public List<MachineTask> calculate(){
        return null;
    }

    protected ConfigInfo produceConfig(UserTask userTask, List<RfCase> list, double val, boolean single){
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
        configInfo = this.produceBaseConfig(userTask,configInfo);
        if(!EqualUtil.isNull(tag)){
            configInfo.setComponentTags(tag.substring(0, tag.length()-1));
        }
        if(!EqualUtil.isNull(singleTag)){
            configInfo.setSingleCaseTag(singleTag.substring(0, singleTag.length()-1));
        }

        configInfo.setTimeSum(sum);
        configInfo.setCaseCount(count);

        return configInfo;
    }

    protected ConfigInfo produceBaseConfig(UserTask userTask, ConfigInfo configInfo){
        configInfo.setTopLevel(userTask.getCaseGrade());
        configInfo.setNormalRfMail(userTask.getNormalRfMail());
        configInfo.setValgrindMail(userTask.getValgrindMail());
        configInfo.setValgrindFunc(userTask.getValgrindFunc());
        configInfo.setProductTags(userTask.getProject());
        configInfo.setExcludeTags(userTask.getExcludeTags());
        configInfo.setTaskId(userTask.getTaskId());
        configInfo.setGcovParam(userTask.getGcovParam());
        return configInfo;
    }

    protected MachineTask createMachineTask(ConfigInfo configInfo,UserTask userTask){
        MachineTask machineTask = new MachineTask();
        machineTask.setConfig(configInfo.toString());
        machineTask.setCaseCount(configInfo.getCaseCount());
        machineTask.setExpectTime(configInfo.getTimeSum());
        machineTask.setComponentTags(configInfo.getComponentTags());
        machineTask.setSingleCaseTag(configInfo.getSingleCaseTag());

//        machineTask.setUserTask(userTask);
        machineTask.setExcludeTags(userTask.getExcludeTags());
        machineTask.setTopLevel(userTask.getCaseGrade());
        machineTask.setProductTags(userTask.getProductTags());
        machineTask.setProject(userTask.getProject());
        machineTask.setTaskType(userTask.getTaskType());
        machineTask.setRetentionTime(userTask.getRetentionTime());
        machineTask.setMachineType(userTask.getMachineType());
        return machineTask;
    }

}
