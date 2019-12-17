package com.yhh.frame.framework.vo;

/***
 * 运行结果实体类
 * @param <R>
 */
public class TaskResult<R> {

    private final TaskResultType taskResultType; //方法是否成功完成
    private final R returnValue;  //方法处理返回结果
    private final String reason; //如果方法失败,这里填充原因

    public TaskResult(TaskResultType taskResultType, R returnValue, String reason) {
        super();
        this.taskResultType = taskResultType;
        this.returnValue = returnValue;
        this.reason = reason;
    }

    /***
     * 方法运行成功
     * @param taskResultType
     * @param returnValue
     */
    public TaskResult(TaskResultType taskResultType, R returnValue) {
        super();
        this.taskResultType = taskResultType;
        this.returnValue = returnValue;
        this.reason = "SUCCESS";
    }

    public TaskResultType getTaskResultType() {
        return taskResultType;
    }

    public R getReturnValue() {
        return returnValue;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "taskResultType=" + taskResultType +
                ", returnValue=" + returnValue +
                ", reason='" + reason + '\'' +
                '}';
    }
}
