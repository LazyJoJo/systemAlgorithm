package com.ruijie.cpu.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * <p>Description: 组件包对应用例信息表</p>
 * <p>Create Time: 2019/7/18 </p>
 * @author zhengchengbin
 * @param
 */
@Data
@Entity
@Table(name="rf_case")
@EqualsAndHashCode(of = {"name","longName","com","comp","sumTime","count","tag"})
public class RfCase implements Comparable<RfCase> {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name" , nullable = false, length = 128)
    private String name;
    @Column(name = "longname" , nullable = false, length = 256)
    private String longName;
    @Column(name = "product_tag" , nullable = false, length = 256)
    private String productTag; // 用例类别 switch 分割符：|
    @Column(name = "tag" ,  length = 256)
    private String tag;   // TODO 单用例tag 根据tag判断要执行的用例，casename就不需要了，用例级别单独一个框填写

    @Column(name = "com" ,  length = 64)
    private String com;   // 组件
    @Column(name = "comp" ,  length = 64)
    private String comp;        //组件包

    @Column(name = "level_min" ,  length = 12)
    private boolean level_min;      //级别
    @Column(name = "level0" ,  length = 12)
    private boolean level0;      //级别
    @Column(name = "level1" ,  length = 12)
    private boolean level1;      //级别
    @Column(name = "level2" ,  length = 12)
    private boolean level2;      //级别
    @Column(name = "level3" ,  length = 12)
    private boolean level3;      //级别
    @Column(name = "srs" ,  length = 12)
    private boolean srs;      //级别
    @Column(name = "spec" ,  length = 12)
    private boolean spec;      //级别

    @Column(name = "l_time" ,  length = 64)
    private int longTime;      //级别
    @Column(name = "s_time" ,  length = 64)
    private int shortTime;      //级别

    //    @Column(name = "x86", length = 12)
//    private boolean x86;
    @Column(name = "nox86", length = 12)
    private boolean nox86;

    // 临时统计变量
    @Transient
    private int sumTime;
    @Transient
    private int count;


    @Override
    public int compareTo(RfCase rfCase) {           //重写Comparable接口的compareTo方法，
        return this.sumTime -  rfCase.getSumTime();// 根据年龄升序排列，降序修改相减顺序即可
    }


}

