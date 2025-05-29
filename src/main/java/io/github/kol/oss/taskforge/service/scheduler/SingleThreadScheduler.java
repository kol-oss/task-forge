package io.github.kol.oss.taskforge.service.scheduler;

public class SingleThreadScheduler extends PoolScheduler {
    public SingleThreadScheduler() {
        super(1);
    }
}
