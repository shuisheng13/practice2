package com.yhh.frame.test;

import com.yhh.frame.framework.vo.ITaskProcesser;
import com.yhh.frame.framework.vo.TaskResult;
import com.yhh.frame.framework.vo.TaskResultType;

import java.util.Random;

/***
 * 业务处理任务类
 * 具体执行业务放入 taskExecute
 */
public class MaTask implements ITaskProcesser<Integer,Integer> {
    @Override
    public TaskResult<Integer> taskExecute(Integer data) {
        Random r = new Random();
        int flag = r.nextInt(500);
        Integer returnVal =  data.intValue()+flag;
        if(flag<=700){
            return new TaskResult<>(TaskResultType.SUCCESS,returnVal);
        }else if (flag>900&&flag<990){
            return new TaskResult<>(TaskResultType.FAILURE,returnVal,"FAIL");
        }else{
            return new TaskResult<>(TaskResultType.EXCEPTION,returnVal,"exception");
        }
    }
}
