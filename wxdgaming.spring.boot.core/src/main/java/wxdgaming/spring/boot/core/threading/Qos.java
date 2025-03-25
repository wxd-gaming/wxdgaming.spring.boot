package wxdgaming.spring.boot.core.threading;

import com.google.common.base.Supplier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.Throw;

import java.util.concurrent.CompletableFuture;

/**
 * 自动重试类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-01-30 11:23
 */
@Getter
@Setter
@Accessors(chain = true)
public class Qos {

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public static void retryRun(int retry, Runnable runnable) {
        if (retry < 1) retry = 1;
        Throwable throwable = null;
        for (int i = 0; i < retry; i++) {
            try {
                runnable.run();
                return;
            } catch (Throwable e) {
                throwable = e;
            }
        }
        throw Throw.of("重试次数：" + retry, throwable);
    }

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public static CompletableFuture<Void> retryRunAsync(int retry, Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            retryRun(retry, runnable);
        });
    }

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public static <R> R retrySupply(int retry, Supplier<R> runnable) {
        if (retry < 1) retry = 1;
        Throwable throwable = null;
        for (int i = 0; i < retry; i++) {
            try {
                return runnable.get();
            } catch (Throwable e) {
                throwable = e;
            }
        }
        throw Throw.of("重试次数：" + retry, throwable);
    }

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public static <R> CompletableFuture<R> retrySupplyAsync(int retry, Supplier<R> runnable) {
        return CompletableFuture.supplyAsync(() -> retrySupply(retry, runnable));
    }

    /** 重试次数  默认一次 */
    int retry = 1;

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public void qosRun(Runnable runnable) {
        retryRun(retry, runnable);
    }

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public CompletableFuture<Void> qosRunAsync(Runnable runnable) {
        return retryRunAsync(retry, runnable);
    }

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public <R> R qosSupply(Supplier<R> supplier) {
        return retrySupply(retry, supplier);
    }

    /** 保证质量的运行，如果异常重试，比如http超时，rpc超时 */
    public <R> CompletableFuture<R> qosSupplyAsync(Supplier<R> supplier) {
        return retrySupplyAsync(retry, supplier);
    }

}
