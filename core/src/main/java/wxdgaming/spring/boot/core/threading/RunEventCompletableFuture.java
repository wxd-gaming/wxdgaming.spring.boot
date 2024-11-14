package wxdgaming.spring.boot.core.threading;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 16:12
 **/
@Getter
class RunEventCompletableFuture extends Event {

    static RunEventCompletableFuture of(Supplier runnable) {
        return new RunEventCompletableFuture(runnable);
    }

    CompletableFuture future = new CompletableFuture<>();
    private final Supplier runnable;

    public RunEventCompletableFuture(Supplier runnable) {
        this.runnable = runnable;

    }

    @Override public void onEvent() throws Throwable {
        try {
            Object object = runnable.get();
            future.complete(object);
        } catch (Throwable e) {
            future.completeExceptionally(e);
        }
    }
}
