package io.github.kol.oss.taskforge.core.descriptors;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IStatus;

public interface IDescriptors<T> {
    IAction<T> getAction();

    ICancelToken getCancelToken();

    IScheduler getScheduler();

    IStatus getStatus();

    T getResult();

    void setResult(T result);

    Exception getException();

    void setException(Exception error);
}
