package io.github.kol.oss.taskforge.service.scheduler;

import io.github.kol.oss.taskforge.core.scheduler.IScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoolScheduler implements IScheduler {
    protected volatile int concurrencyLevel;
    protected volatile ExecutorService pool;

    public PoolScheduler() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public PoolScheduler(final int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        this.pool = Executors.newFixedThreadPool(concurrencyLevel);
    }

    @Override
    public void schedule(Runnable action) {
        this.pool.submit(action);
    }

    @Override
    public void close() throws Exception {
        this.pool.shutdown();
    }

    public int getConcurrencyLevel() {
        return this.concurrencyLevel;
    }
}
