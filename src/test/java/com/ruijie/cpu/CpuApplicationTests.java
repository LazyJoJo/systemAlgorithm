//package com.ruijie.cpu;
//
//import com.ruijie.cpu.entity.RfCase;
//import com.ruijie.cpu.entity.UserTask;
//import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
//import lombok.extern.slf4j.Slf4j;
//
//import org.jasypt.encryption.StringEncryptor;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CpuApplication.class)  //指定需要测试的应用
//@EnableJpaRepositories
//@EnableAutoConfiguration
//@EnableAsync
//@EnableTransactionManagement
//@EnableEncryptableProperties
//public class CpuApplicationTests {
//
//    @Autowired
//    StringEncryptor encryptor;
//
//    @Test
//    public void contextLoads() {
//        String num = "autotest";
//        String snum = encryptor.encrypt(num);
//        System.out.println(snum);
//    }
//
////    @Test
//    public  void  templateRestApiTest(){
////        Map<String,List<RfCase>> sourceMap = new HashMap<String, List<RfCase>>();
////        RfCase rfCase = new RfCase();
////        List<RfCase> rfCases = new ArrayList<>();
////        rfCases.add(rfCase);
//
//        UserTask userTask = new UserTask();
//        RfCase rfCase = new RfCase();
//        List<RfCase> list = new ArrayList<>();
//        list.add(rfCase);
//        Map<String,List<RfCase>> map = new HashMap<>();
//        map.put("test",list);
//        userTask.setRfcaseMap(map);
//        Map<String,List<RfCase>>  mapcase = (Map<String,List<RfCase>>)userTask.getRfcaseMap();
//        System.out.println(mapcase);
//
////        template.getForObject("",map)
////        List<Integer> list = new ArrayList<>();
////        List<Number> list2 = new ArrayList<>();
////        showKeyValue(list);
////        showKeyValue(list2);
//
//    }
//
////    public <T> void showKeyValue(?){
////        System.out.println(obj.size());
////    }
//
//
//
//
//
//
//}
