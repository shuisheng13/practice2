package com.yhh.frame.vo;

import java.util.concurrent.TimeUnit;

/***
 * 线程休眠工具类
 */
public class SleepTool {

    /***
     *  按秒休眠
     */

    public static final  void second(int second){
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final void ms(int ms){

        try {
            TimeUnit.MICROSECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




}
