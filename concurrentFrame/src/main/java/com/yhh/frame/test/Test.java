package com.yhh.frame.test;

import com.yhh.frame.framework.PendingJobPool;
import com.yhh.frame.framework.vo.TaskResult;
import com.yhh.frame.vo.SleepTool;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Random;

public class Test {
    private final static String jobName= "计算任务";
    private final static int JOB_LENGTH = 5000;


    public static class DetailTask<R> implements  Runnable{
        private PendingJobPool pendingJobPool;
        private String jobName;

        public DetailTask(PendingJobPool pendingJobPool, String jobName) {
            this.pendingJobPool = pendingJobPool;
            this.jobName = jobName;
        }

        @Override
        public  void run() {
            List<TaskResult<R>> resultList =  pendingJobPool.getTaskDetail(jobName);
            for(TaskResult taskResult:resultList){
                TaskResult<R> result = resultList.get(0);
                System.out.println(result);
            }
        }
    }
    public static class PlanTask implements Runnable{

        private PendingJobPool pendingJobPool;
        private String jobName;

        public PlanTask(PendingJobPool pendingJobPool, String jobName) {
            this.pendingJobPool = pendingJobPool;
            this.jobName = jobName;
        }

        @Override
        public void run() {
            for (;;){
                System.out.println(pendingJobPool.getTaskPlan(jobName));
            }
        }
    }



    public static <R> void main(String[] args) {

        MaTask maTask = new MaTask();
        PendingJobPool pendingJobPool = PendingJobPool.getInstance();
        pendingJobPool.register(jobName,JOB_LENGTH,maTask,20);
        Random random = new Random();
        for (int i=0;i<JOB_LENGTH;i++){
            pendingJobPool.putTask(jobName,random.nextInt(500));
        }
        DetailTask detailTask = new DetailTask(pendingJobPool,jobName);
        PlanTask planTask = new PlanTask(pendingJobPool,jobName);
        SleepTool.ms(100);
        new Thread(detailTask).start();
        new Thread(planTask).start();
    }


}
