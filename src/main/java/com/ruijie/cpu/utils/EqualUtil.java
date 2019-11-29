package com.ruijie.cpu.utils;



public class EqualUtil {
    public static boolean equal(Object a, Object b){
        if (a==null&&b==null){
            return true;
        }
        if (a!=null&&b!=null){
            if(a.equals(b)){
                return true;
            }
        }
        return false;
    }

    public static boolean isNull(String a){
        if (a==null||a.equals("")){
            return true;
        }else{
            return false;
        }
    }
}
