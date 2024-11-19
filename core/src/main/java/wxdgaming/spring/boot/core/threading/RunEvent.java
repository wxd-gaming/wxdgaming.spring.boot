package wxdgaming.spring.boot.core.threading;

/**
 * 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 16:12
 **/
class RunEvent extends Event {

    static Event of(int stackTrace, Runnable runnable) {
        Event event;
        if (!(runnable instanceof Event)) {
            event = new RunEvent(stackTrace, runnable);
        } else {
            event = (Event) runnable;
        }
        return event;
    }

    private final Runnable runnable;

    public RunEvent(int stackTrace, Runnable runnable) {
        super(stackTrace);
        this.runnable = runnable;
    }

    @Override public void onEvent() throws Throwable {
        runnable.run();
    }
}
