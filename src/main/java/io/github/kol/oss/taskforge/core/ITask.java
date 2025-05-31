package io.github.kol.oss.taskforge.core;

import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

import java.util.concurrent.TimeUnit;

public interface ITask<T> {
    void start();

    void cancel();

    void await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    T getResult() throws Exception;

    Exception getException();

    TaskState getState();

    IStatus getStatus();

    <N> ITask<N> then(ITask<N> task);
}
