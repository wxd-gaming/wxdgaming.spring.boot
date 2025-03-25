package wxdgaming.spring.boot.core.threading;


/** 取消 */
public interface Job {

    /** 获取名字 */
    String names();

    /** 取消 */
    boolean cancel();

}
