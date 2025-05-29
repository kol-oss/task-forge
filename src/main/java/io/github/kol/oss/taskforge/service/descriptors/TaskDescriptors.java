package io.github.kol.oss.taskforge.service.descriptors;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IStatus;

public class TaskDescriptors<T> implements IDescriptors<T> {
    protected IAction<T> action;
    protected ICancelToken token;
    protected IScheduler scheduler;
    protected IStatus status;
    protected T result;
    protected Exception error;

    public TaskDescriptors(IAction<T> action, ICancelToken token, IScheduler scheduler, IStatus status, T result, Exception error) {
        this.action = action;
        this.token = token;
        this.scheduler = scheduler;
        this.status = status;
        this.result = result;
        this.error = error;
    }

    @Override
    public synchronized IAction<T> getAction() {
        return this.action;
    }

    @Override
    public synchronized ICancelToken getCancelToken() {
        return this.token;
    }

    @Override
    public synchronized IScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public synchronized IStatus getStatus() {
        return this.status;
    }

    @Override
    public synchronized T getResult() {
        return this.result;
    }

    @Override
    public synchronized void setResult(T result) {
        this.result = result;
    }

    @Override
    public synchronized Exception getException() {
        return this.error;
    }

    @Override
    public synchronized void setException(Exception error) {
        this.error = error;
    }
}
