package com.ruijie.cpu.algorithm.factory;

import com.ruijie.cpu.entity.RfCase;
import com.ruijie.cpu.entity.UserTask;
import com.ruijie.cpu.entity.mysqlEntity.CaseGroupAns;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TagAnalyseService {



    /**
     * <p>Description: 获取每个组件用时</p>
     * <p>Create Time: 2019/7/24 </p>
     * @author zhengchengbin
     * @param
     */
    Map<String, List<RfCase>> tagAnalysis(UserTask userTask);

    List<RfCase> getCaseTime(List<CaseGroupAns> ans, String level);

    void MergeCase(Map<String, RfCase> map, CaseGroupAns ans);

}
