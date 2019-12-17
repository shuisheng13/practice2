package com.yhh.frame.framework;

import com.yhh.frame.framework.vo.ITaskProcesser;
import com.yhh.frame.framework.vo.TaskResult;
import com.yhh.frame.framework.vo.TaskResultType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/***
 * 对外提供使用的框架类
 * 使用者直接面对对象
 * 交给spring管理对象
 */
//@Component
public class PendingJobPool{
    //线程池大小, 系统核数
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    //任务队列(有界阻塞队列,如无界容易引起频繁GC)
    private static BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(5000);
    //线程池
    private static ExecutorService executorService  = new ThreadPoolExecutor(THREAD_COUNT,THREAD_COUNT,
            60,TimeUnit.SECONDS,taskQueue);
    //job 存放容器，线程隔离
    private static ConcurrentHashMap<String,JobInfo<?>> jobInfoConcurrentHashMap = new ConcurrentHashMap<>();
    //频闭构造函数，防止用户频繁构造
    private PendingJobPool(){};
    public static ConcurrentHashMap<String, JobInfo<?>> getJobInfoConcurrentHashMap() {
        return jobInfoConcurrentHashMap;
    }

    public static PendingJobPool getInstance(){
        return new PendingJobPool();
    }

    /***
    /***
     * 内部类，将任务及任务处理器封装成runnable(线程可执行方法)
      */
    public static class PendTask<R,T> implements Runnable{
        //构造函数中传参
        private JobInfo<R> jobInfo;
        private T t;  //子类执行方法所需参数
        public PendTask(JobInfo<R> jobInfo, T t) {
            this.jobInfo = jobInfo;
            this.t = t;
        }

        @Override
        public void run() {
            //执行run方法实际是执行子类实现的具体任务处理方法
            R r = null;
            ITaskProcesser<T,R> iTaskProcesser = (ITaskProcesser<T, R>) this.jobInfo.getiTaskProcesser();
            TaskResult<R> taskResult = null;
            try{
                taskResult=iTaskProcesser.taskExecute(t);
                if(null==taskResult){
                    taskResult = new TaskResult<R>(TaskResultType.EXCEPTION,r,"result is null");
                }
                if(null==taskResult.getTaskResultType()){
                    if(null==taskResult.getReason()){
                        taskResult = new TaskResult<R>(TaskResultType.EXCEPTION,r,"reason is null");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                taskResult = new TaskResult<R>(TaskResultType.EXCEPTION,r,e.getMessage());
            }
            //执行完将结果放入结果队列
            jobInfo.addTaskResult(taskResult);
        }
    }

    /***
     * 获取任务
     * @param jobName
     * @param <R>
     * @return
     */
    public <R> JobInfo<R> getJoB(String jobName){
         JobInfo<R> jobInfo = (JobInfo<R>) jobInfoConcurrentHashMap.get(jobName);
         if(null==jobInfo){
             throw new RuntimeException("非法任务");
         }
         return jobInfo;
    }
    /***
     * 添加任务
     * 获取jobInfo，对象
     * 将jonInfo 及数据 封装成 pendTask
     * 将pendTask 丢进线程池
     */
    public<R,T> void putTask(String jobName,T t){
       JobInfo<R> jobInfo = (JobInfo<R>) getJoB(jobName);
       PendTask<R,T> pendTask = new PendTask<>(jobInfo,t);
       executorService.execute(pendTask);
    }

    /***
     * 注册任务(最重要一步)
     * 用户所用
     */
    public <R> void register(String jobName,int jobLength,ITaskProcesser<?,?> iTaskProcesser,long expireTime){
        //给用户创建个jobInfo对象
        JobInfo<R> jobInfo = new JobInfo<R>(jobName,jobLength,iTaskProcesser,expireTime);
        if(jobInfoConcurrentHashMap.putIfAbsent(jobName,jobInfo)!=null){
            throw new RuntimeException("任务已注册");
        }
    }
    /***
     * 获取每个任务的具体详情
     */
    public <R> List<TaskResult<R>> getTaskDetail(String jobName){
            JobInfo<R> jobInfo = getJoB(jobName);
            List<TaskResult<R>> taskResults = jobInfo.getTaskResult();
            return taskResults;
    }

    /***
     * 获取某个任务的具体进度
     */
    public <R> String getTaskPlan(String jobName){
        JobInfo<R> jobInfo = getJoB(jobName);
        return jobInfo.getTaskPlan();
    }
}
