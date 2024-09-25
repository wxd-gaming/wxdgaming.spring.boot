package wxdgaming.spring.boot.core.threading;

/**
 * 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 16:12
 **/
class RunEvent extends Event {

    static Event of(Runnable runnable) {
        Event event;
        if (!(runnable instanceof Event)) {
            event = new RunEvent(runnable);
        } else {
            event = (Event) runnable;
        }
        return event;
    }

    private final Runnable runnable;

    public RunEvent(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override public void onEvent() throws Throwable {
        runnable.run();
    }
}
