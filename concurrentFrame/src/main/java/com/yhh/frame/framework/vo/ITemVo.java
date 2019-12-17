package com.yhh.frame.framework.vo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/***
 * 存放队列的对象
 * @param <T>
 */
public class ITemVo<T>  implements Delayed {

    private long activeTimer;  //队列存活时间
    private T t;    //具体对象

    public ITemVo(long activeTimer, T t) {

        this.activeTimer = TimeUnit.NANOSECONDS.convert(activeTimer,
                TimeUnit.MILLISECONDS)+System.nanoTime();//将传入的时长转换为超时的时刻
        this.t = t;
    }
    public long getActiveTimer() {
        return activeTimer;
    }
    public T getT() {
        return t;
    }
    //返回元素的剩余时间
    @Override
    public long getDelay(TimeUnit unit) {
        long d = unit.convert(this.activeTimer-System.nanoTime(),
                TimeUnit.NANOSECONDS);
        return d;
    }
    //按照剩余时间排序
    @Override
    public int compareTo(Delayed o) {
        long d = getDelay(TimeUnit.NANOSECONDS)-o.getDelay(TimeUnit.NANOSECONDS);
        return (d==0)?0:((d>0)?1:-1);
    }
}
