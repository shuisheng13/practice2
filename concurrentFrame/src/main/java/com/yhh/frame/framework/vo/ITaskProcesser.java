package com.yhh.frame.framework.vo;

import java.io.IOException;

/***
 * 框架任务类封装接口
 * 要求框架调用者实现接口,具体任务调用逻辑在接口子类具体实现
 * 参数类型和返回类型均为泛型
 */
public interface ITaskProcesser<T,R> {

    TaskResult<R> taskExecute(T data) throws IOException;

}
