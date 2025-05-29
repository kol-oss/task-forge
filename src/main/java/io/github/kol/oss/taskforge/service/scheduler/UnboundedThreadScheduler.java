package io.github.kol.oss.taskforge.service.scheduler;

import io.github.kol.oss.taskforge.core.scheduler.IScheduler;

public class UnboundedThreadScheduler implements IScheduler {
    @Override
    public void schedule(Runnable action) {
        new Thread(action).start();
    }

    @Override
    public void close() throws Exception {

    }
}
