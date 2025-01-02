package wxdgaming.spring.boot.core.threading;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * 带回调
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 15:02
 **/
public abstract class EventCallable<V> extends Event implements Runnable, Callable<V> {

    CompletableFuture<V> future = new CompletableFuture<>();

    public EventCallable() {
        super(4);
    }

    public EventCallable(int stackTrace) {
        super(stackTrace);
    }

    @Override
    protected void onEvent() throws Throwable {
        try {
            V call = call();
            future.complete(call);
        } catch (Throwable throwable) {
            future.completeExceptionally(throwable);
        }
    }

}
