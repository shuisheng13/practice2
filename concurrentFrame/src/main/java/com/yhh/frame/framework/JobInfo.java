package com.yhh.frame.framework;

import com.yhh.frame.framework.vo.ITaskProcesser;
import com.yhh.frame.framework.vo.TaskResult;
import com.yhh.frame.framework.vo.TaskResultType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 框架工作的实体类
 * @param <R>
 */
public class JobInfo<R> {

    private final String jobName;  //工作名
    private final int jobLength;  //工作中的任务列表
    private final ITaskProcesser<?,?> iTaskProcesser; //处理工作的任务处理器
    private AtomicInteger successCount; //任务的成功次数
    private AtomicInteger failCount; //任务的失败次数
    private AtomicInteger taskCount; //任务总执行数
    private LinkedBlockingDeque<TaskResult<R>> linkedBlockingDeque;  //执行结果存入队列
    private final long expTimer;  //任务结果查询的最大时限

    public JobInfo(String jobName, int jobLength, ITaskProcesser<?, ?> iTaskProcesser,long expTimer) {
        this.jobName = jobName;
        this.jobLength = jobLength;
        this.iTaskProcesser = iTaskProcesser;
        this.successCount = new AtomicInteger(0); //初始化成功次数为0
        this.failCount = new AtomicInteger(0);  //初始化失败次数为0
        this.linkedBlockingDeque = new LinkedBlockingDeque<>(jobLength);  //初始化查询结果队列为最大任务个数
        this.expTimer = expTimer;
        this.taskCount = new AtomicInteger(0);
    }
    /***
     * 获取任务执行成功数
     */
    public int getSuccessCount() {
        return successCount.get();
    }

    /***
     * 获取任务执行失败数
     * @return
     */
    public int getFailCount() {
        return failCount.get();
    }
    //获取任务执行处理器
    public ITaskProcesser<?, ?> getiTaskProcesser() {
        return iTaskProcesser;
    }

    /***
     * 获取任务总执行数
     * @return
     */
    public int getTaskCount(){
        return taskCount.get();
    }
    /***
     * 获取任务结果
     */
    public List<TaskResult<R>> getTaskResult (){
        List<TaskResult<R>> results = new ArrayList<>();
        TaskResult<R> task;
        while ((task=linkedBlockingDeque.pollFirst())!=null){
            results.add(task);
        }
        return results;
    }
    /***
     * 添加任务处理结果
     * 成功或失败次数加减
     * 将任务处理结果放入容器中
     *
     */
    public void addTaskResult(TaskResult<R> taskResult){

        if(taskResult.getTaskResultType().equals(TaskResultType.SUCCESS)) {
            successCount.incrementAndGet();
        }else if(taskResult.getTaskResultType().equals(TaskResultType.FAILURE)){
            failCount.incrementAndGet();
        }
        taskCount.incrementAndGet();
        linkedBlockingDeque.addLast(taskResult);
        //如果任务执行次数==总任务数
        if(taskCount.get()==jobLength){
            CheckJobProcesser.JobProcesserHandler.checkJobProcesser.getInstance().putJob(jobName,expTimer);
        }
    }

    public String getTaskPlan() {
        return "任务 <"+jobName+">"+"执行进度  总任务数 ： "+taskCount +"  成功执行数 : "+successCount+
        "  失败数 : "+failCount;
    }
}
