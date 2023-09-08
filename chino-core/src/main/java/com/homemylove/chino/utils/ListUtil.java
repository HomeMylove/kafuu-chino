package com.homemylove.chino.utils;

import java.util.List;
import java.util.Random;

public class ListUtil {

    /**
     * 随机获取一个元素
     * @return 随机元素
     * @param <T> 类型
     */
    public static  <T>  T getRandomEle(List<T> list){
        if(list==null) return null;
        int length = list.size();
        if(length==0) return null;
        return list.get(getRandomInt(length));
    }

    public static <T> T getRandomEle(T[] arr){
        if(arr==null) return null;
        int length = arr.length;
        if(length == 0) return null;
        return arr[getRandomInt(length)];
    }

    private static int getRandomInt(int max){
        return getRandomInt(0,max);
    }

    private static int getRandomInt(int min,int max){
        return new Random().nextInt(max - min) + min;
    }
}
