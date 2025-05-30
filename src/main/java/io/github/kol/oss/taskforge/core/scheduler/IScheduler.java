package io.github.kol.oss.taskforge.core.scheduler;

public interface IScheduler {
    void schedule(Runnable action);

    void close();
}
