package com.ruijie.cpu.algorithm.factory;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
public class AlgorithmFactory  {


    /**
     * <p>Description: spring 自动将Algorithm的所有子类映射到map中，不清楚UserTask是怎么映射的</p>
     * <p>Create Time: 2019/12/11 </p>
     * @author zhengchengbin
     * @param
     */
    @Autowired
    private Map<String, Algorithm> factoryMap;

    public Algorithm getBy(String entNum) {
        return factoryMap.get(entNum);
    }

}
