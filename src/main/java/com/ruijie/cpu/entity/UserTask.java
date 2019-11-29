package com.ruijie.cpu.entity;

import com.ruijie.cpu.utils.EqualUtil;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: 任务信息表</p>
 * <p>Create Time: 2019/7/18 </p>
 * @author zhengchengbin
 * @param
 */
@Entity
@Table(name = "user_task")
@Data
@ToString(exclude = { "machineTasks" })
//@EqualsAndHashCode(exclude = {"machineTasks"})
public class UserTask implements Cloneable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)  //默认的方式，交由数据库自己控制
    private Long  tid;

    @Column(name = "state" ,  length = 64)
    private String state;      // wait,run,queue,cancel,close,bug,fail
    @Column(name = "begin_time" ,  length = 64)
    private String beginTime;
    @Column(name = "end_time" ,  length = 64)
    private String endTime;    // 要统计每台机子的任务结束时间，去最长的，为总任务的结束时间
    @Column(name = "estimate_time" ,  length = 64)
    private String estimateTime;   // 用例估计运行时间
    @Column(name = "need_finish_time" ,  length = 64)
    private String needFinishTime; // 规定完成时间
    @Column(name = "project" ,  length = 64)
    private String project;   // 哪个项目
    @Column(name = "version" ,  length = 1024)
    private String version;   // 版本
    @Column(name = "com_version" ,  length = 1024)
    private String comVersion;   // 单组件版本


    @Column(name = "case_tag" ,  length = 64)
    private String caseTag;   // 执行用例标签  有可能是all
    @Column(name = "exclude_tags" , length = 64)
    private String excludeTags;  // 排除用例标签
    @Column(name = "case_grade" , length = 64)
    private String caseGrade;    // 跑用例级别 -1~3

    @Column(name = "single_case_tags" , columnDefinition= "text")
    private String singleCaseTags;  //支持用例级别的标签，与组件级别和组件包级别分开填写

    @Column(name = "cases_num" , length = 64)
    private String casesNum;   // 用例数量---系统采集

    @Column(name = "user" , length = 64)
    private String user;       // 归属人---系统采集

    @Column(name = "valgrind_func" , length = 64)
    private String valgrindFunc;   // 是否开启valgrind检测
    @Column(name = "valgrind_mail" , length = 64)
    private String valgrindMail;   // 是否开启valgrind发邮件功能
    @Column(name = "normal_rf_mail" , length = 64)
    private String normalRfMail;   // 是否开启发邮件功能

    @Column(name = "product_tags" ,length = 64)
    private String productTags;   // switch 等

    @Column(name = "run_type" , length = 64)
    private String runType;    // 任务执行优先级
    @Column(name = "retention_time" , length = 64)
    private String retentionTime; // 环境保留时间(产生bug的才保留环境)
    @Column(name = "task_type" , length = 64)
    private String taskType;   // daily,test,workTest,dailyHistory
    @Column(name = "bug_id" , length = 64)
    private String bugId;      // bug id
    @Column(name = "machine_num" , length = 64)
    private String machineNum;  //占用机子数量 ---系统自动分配 （不记录具体的机器，可以通过查询获得，只记录数量，回收了数量就减掉）
    @Column(name = "task_success" , length = 64)
    private String taskSuccess;  //当前机器池是否满足需求（未达成时，分析并调整规划）
    @Column(name = "machine_type",length = 64)
    private String machineType;   //机器类别，x86-64 等
    @Column(name="method_type",length = 16)
    private String methodType;     //时间优先还是空间优先 ，调度方法的类型

    @Column(name="rounds",length = 8)
    private Long rounds;  //轮次 每日冒烟使用
    @Column(name = "main_tag",length = 64)
    private String mainTag;    // valgrind,normal,vsu

    @Column(name = "task_id",length = 64)
    private String taskId;   //组件化任务触发过来的关联id

    @Transient
    private Map<String, List<RfCase>> rfcaseMap;


//    @OneToMany(mappedBy="userTask", cascade={CascadeType.MERGE,CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true) //外健交给多方维护
//    private Set<MachineTask> machineTasks = new HashSet<>();
//
//    public void addMachineTask(MachineTask machineTask) {
//        machineTasks.add(machineTask);
//        machineTask.setUserTask(this);
//    }
//
//    public void removeMachineTask(MachineTask machineTask) {
//        machineTasks.remove(machineTask);
//        machineTask.setUserTask(null);
//    }


    @Override
    public UserTask clone() {
        UserTask userTask = null;
        try{
            userTask = (UserTask) super.clone();   //浅复制
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return userTask;
    }

    @Override
    public boolean equals(Object obj) {
        UserTask other = (UserTask) obj;
        if (!EqualUtil.equal(this.project,other.getProject())){
            return false;
        }
        if (!EqualUtil.equal(this.version,other.getVersion())){
            return false;
        }
        if (!EqualUtil.equal(this.taskType,other.getTaskType())){
            return false;
        }
        if (!EqualUtil.equal(this.caseGrade,other.getCaseGrade())){
            return false;
        }
        if (!EqualUtil.equal(this.caseTag,other.getCaseTag())){
            return false;
        }
        if (!EqualUtil.equal(this.productTags,other.getProductTags())){
            return false;
        }
        if (!EqualUtil.equal(this.singleCaseTags,other.getSingleCaseTags())){
            return false;
        }
        if (!EqualUtil.equal(this.machineType,other.getMachineType())){
            return false;
        }
        if (!EqualUtil.equal(this.valgrindFunc,other.getValgrindFunc())){
            return false;
        }
        if (!EqualUtil.equal(this.normalRfMail,other.getNormalRfMail())){
            return false;
        }

        return true;
    }


}
