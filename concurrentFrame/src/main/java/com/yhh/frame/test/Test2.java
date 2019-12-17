package com.yhh.frame.test;

import com.yhh.frame.framework.PendingJobPool;
import com.yhh.frame.framework.vo.TaskResult;
import com.yhh.frame.vo.SleepTool;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

public class Test2 {
    private static final int JOB_LENGTH = 1000;
    private static final String JOB_NAME="文件写入任务";
    private static final String oldPath = "E:\\业务图\\SaaS平台.jpg";
    private static String newPath = "D://test//";


    public static class DetailTask<R> implements Runnable{
        private PendingJobPool pendingJobPool;

        public DetailTask(PendingJobPool pendingJobPool) {
            this.pendingJobPool = pendingJobPool;
        }

        @Override
        public void run() {
            List<TaskResult<R>> list =  pendingJobPool.getTaskDetail(JOB_NAME);
            for (int i = 0; i <list.size() ; i++) {
                TaskResult<R> taskResult = list.get(i);
                System.out.println(taskResult);
            }
        }
    }
    public static class PlanTask<R> implements Runnable{
        private PendingJobPool pendingJobPool;

        public PlanTask(PendingJobPool pendingJobPool) {
            this.pendingJobPool = pendingJobPool;
        }

        @Override
        public void run() {
            String detail = pendingJobPool.getTaskPlan(JOB_NAME);
            System.out.println(detail);
        }
    }
    public static void main(String[] args) throws IOException {
        FileTask fileTask = new FileTask(oldPath,newPath);
        PendingJobPool pendingJobPool = PendingJobPool.getInstance();
        pendingJobPool.register(JOB_NAME,JOB_LENGTH,fileTask,20);
        for (int i = 0; i <JOB_LENGTH ; i++) {
            pendingJobPool.putTask(JOB_NAME,i);
        }
        SleepTool.second(5);
        new Thread(new DetailTask(pendingJobPool)).start();
        new Thread(new PlanTask(pendingJobPool)).start();
        }
    }
