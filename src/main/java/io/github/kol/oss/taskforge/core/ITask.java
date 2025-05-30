package io.github.kol.oss.taskforge.core;

import io.github.kol.oss.taskforge.core.status.IStatus;

public interface ITask<T> {
    void start();

    void cancel();

    T getResult() throws Exception;

    Exception getException();

    IStatus getStatus();

    <N> ITask<N> then(ITask<N> task);
}
