package io.github.kol.oss.taskforge.core.scheduler;

public interface IScheduler extends AutoCloseable {
    void schedule(Runnable action);
}
