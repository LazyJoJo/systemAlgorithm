package com.ruijie.cpu.entity;

import com.ruijie.cpu.utils.EqualUtil;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * <p>Description: 任务结果记录</p>
 * <p>Create Time: 2019/7/24 </p>
 * @author zhengchengbin
 * @param
 */
@Entity
@Data
@Table(name = "machine_task")
public class MachineTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //默认的方式，交由数据库自己控制
    private Long id;
    //    @Column(name = "tid" ,  length = 64)
//    private Long tid;       // 任务id
//    @Column(name = "mid" ,  length = 64)
//    private Long mid;
    @Column(name = "task_name", length = 64)
    private String taskName;  // 具体job名称
    @Column(name = "net_set", length = 64)
    private String netSet;   // 具体网段
    @Column(name = "task_type", length = 64)
    private String taskType;   // 任务类型

    @Column(name = "project", length = 64)
    private String project; //当前运行项目

    @Column(name = "config", length = 1024)  //build的config
    private String config;

    @Column(name = "task_num", length = 64)
    private Integer taskNum;  //对应那台机上跑任务的编号
    @Column(name = "update_num", length = 64)
    private Integer updateNum;  //对应那台机上跑任务的编号  唯一标识
    @Column(name = "update_ans_url", length = 256)
    private String updateAnsUrl;   //更新URL
    @Column(name = "task_ans_url", length = 256)
    private String taskAnsUrl;     //任务URL

    @Column(name = "state", length = 64)
    private String state;         //状态
    @Column(name = "start_time", length = 64)
    private String startTime;
    @Column(name = "end_time", length = 64)
    private String endTime;          //是否是最新任务 任务结束时修改成当时的时间 'now'

    @Column(name = "top_level", length = 256)
    private String topLevel;  //   用例级别
    @Column(name = "product_tags", length = 256)
    private String productTags;  //  switch
    @Column(name = "component_tags", length = 2048)
    private String componentTags;  // 用例
    @Column(name = "single_case_tag", columnDefinition = "text")
    private String singleCaseTag;  // 单个用例标签
    @Column(name = "exclude_tags", length = 512)
    private String excludeTags;  //   目前没用吧
    @Column(name = "machine_type")
    private String machineType;

    @Column(name = "qlevel", length = 64)
    private Integer qLevel;  // 排队级别

    @Column(name = "expect_time", length = 64)
    private Integer expectTime;
    @Column(name = "case_count", length = 64)
    private Integer caseCount;
    @Column(name = "build_result", length = 64)
    private String buildResult;

    @Column(name = "retention_time", length = 64)
    private String retentionTime; // 环境保留时间(产生bug的才保留环境)

    @Column(name = "bug_type", length = 128)
    private String bugType;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "tid")  //本表中外健名称
    private UserTask userTask;

    //取消掉machinePool ，这边的功能用不到


}
