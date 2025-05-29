package io.github.kol.oss.taskforge.core.status;

import java.util.concurrent.TimeUnit;

public interface IEvent {
    void await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    void alert();

    boolean hasAlerted();

    void addListener(Runnable listener);

    void removeListener(Runnable listener);
}
