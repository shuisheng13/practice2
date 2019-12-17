package com.yhh.frame.framework;

import com.yhh.frame.framework.vo.ITemVo;

import java.util.concurrent.DelayQueue;

public class CheckJobProcesser {

    //延时队列
    private static DelayQueue<ITemVo<String>> iTemVos = new DelayQueue<>();

    private CheckJobProcesser() {
    }
    public CheckJobProcesser getInstance(){
        return JobProcesserHandler.checkJobProcesser;
    }
    public static class JobProcesserHandler{
        public static CheckJobProcesser checkJobProcesser = new CheckJobProcesser();
    }
    //任务完成后将任务放入延迟队列
    public void putJob(String jobName,long expireTimer){
        ITemVo<String> iTemVo = new ITemVo<String>(expireTimer,jobName);
        iTemVos.offer(iTemVo);
        System.out.println("Job[\"+jobName+\"已经放入了过期检查缓存，过期时长：\"+expireTime");
    };

    /***
     * 检查过期任务线程
     */
    public static class CheckJob implements Runnable{

        @Override
        public void run() {
            for(;;){
                try {
                    ITemVo<String> itemVo = iTemVos.take();
                    String jobName = itemVo.getT();
                    System.out.println("任务 <> "+jobName+"  已过期，已移除");
                    PendingJobPool.getJobInfoConcurrentHashMap().remove(jobName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static {
        Thread thread = new Thread(new CheckJob());
        thread.setDaemon(true);
        thread.start();
        System.out.println("开启任务过期检查守护线程................");
    }
}
