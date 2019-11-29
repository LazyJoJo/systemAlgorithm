package com.ruijie.cpu.entity;


import lombok.Data;

/**
 * <p>Description: jenkins config 配置信息表</p>
 * <p>Create Time: 2019/7/18 </p>
 * @author zhengchengbin
 * @param
 */
@Data
public class ConfigInfo implements Comparable<ConfigInfo>{

    private String callIp;

    private String valgrindMail;
    private String valgrindFunc;
    private String excludeTags;    // 用例分配时使用
    private String componentTags;  // 用例分配时使用
    private String productTags;
    private String topLevel;   // 跑用例的级别
    private String normalRfMail;   // 正常用例是否发邮件
    private String singleCaseTag;  // 单个用例标签


    private int caseCount; //用例个数
    private int timeSum;   //用例用时估计

    public ConfigInfo(String callIp) {
        this.callIp = callIp;
    }

    @Override
    public int compareTo(ConfigInfo o) {
        return this.timeSum-o.getTimeSum();
    }

    @Override
    public String toString(){
        // 前期准备工作
        String ans ="call D:\\CI\\autotest\\resource\\rglib\\svnupdate.bat\n";
        if("1".equals(this.valgrindFunc)){
            ans+="python D:\\CI\\autotest\\resource\\testbedlib\\valgrind_open_ability.py\n";
        }

        //执行用例部分
        ans+="set autotest=D:\\CI\\autotest\n";
        ans+="set jenkins=%autotest%\\execution\\jenkins\n";
        ans+="python D:\\CI\\autotest\\resource\\testbedlib\\start_task.py %JOB_NAME% %autotest% %jenkins% "+callIp+"\n";

        // 邮件部分
        if("1".equals(this.normalRfMail)){
            ans+="call  D:\\CI\\autotest\\resource\\testbedlib\\automail.bat SWITCH\n";
        }
        if ("1".equals(this.valgrindMail)){
            ans+="set autoSubmitBug=1\n"+
                    "call python D:\\CI\\autotest\\resource\\testbedlib\\valgrind_automail.py %autoSubmitBug%\n";
        }
        ans+="python D:\\CI\\autotest\\resource\\testbedlib\\callBg.py callTaskBack %NODE_NAME% %JOB_NAME% "+ callIp+"\n";
//        System.out.println(callIp);
        return ans;

    }
}
