package org.thingsboard.server.transport.http.utils;

/**
 * Created by Administrator on 2017/12/7.
 */
public class StringUtil {
    public static boolean checkNotNull(String... strs){
        boolean res = true;
        for(String s:strs){
            res = res&& checkNotNull(s);
        }
        return res;
    }
    public static boolean checkNotNull(String str){
        if(str!=null&&!"".equals(str)) return true;
        return false;
    }
}
