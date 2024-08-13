package wxdgaming.spring.boot.core.threading;

/**
 * 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 16:12
 **/
class RunEvent implements Event {

    private final Runnable runnable;

    public RunEvent(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override public final void run() {
        Event.super.run();
    }

    @Override public void onEvent() throws Throwable {
        runnable.run();
    }
}
