package io.github.kol.oss.taskforge.service.scheduler;

import io.github.kol.oss.taskforge.core.scheduler.IScheduler;

public class CurrentThreadScheduler implements IScheduler {
    @Override
    public void schedule(Runnable action) {
        action.run();
    }

    @Override
    public void close() {

    }
}
