package com.ruijie.cpu.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class TimeUtil {

    /**
     * <p>Description: 对时间的加减运算</p>
     * <p>Create Time: 2019/11/4 </p>
     * @author zhengchengbin
     * @param 
     */
    public static String getStatetime(String time, int num, String pattern ,String type)  {

        Calendar c ;
        if (!EqualUtil.isNull(time)){
            c = strToCalendar(time,pattern);
        }else{
            c = Calendar.getInstance();
        }
        if ("day".equals(type)){
            c.add(Calendar.DATE, num);  // add or reduce day
        }
        if("hour".equals(type)){
            c.add(Calendar.HOUR,num);
        }
        if("minute".equals(type)){
            c.add(Calendar.MINUTE,num);
        }
        Date monday = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String preMonday = sdf.format(monday);
        return preMonday;
    }

    public static Calendar strToCalendar(String data,String pattern){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = sdf.parse(data);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }catch (Exception e){
            e.printStackTrace();
            log.error("解析time数据有误");
        }
        return Calendar.getInstance();
    }

    public static Boolean compareTime(String datestr1,String datestr2,String pattern){
        try {
            Calendar calendar1 = strToCalendar(datestr1,pattern);
            Calendar calendar2 = strToCalendar(datestr2,pattern);
            if (calendar1.before(calendar2)){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
