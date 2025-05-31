package io.github.kol.oss.taskforge.service;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.scheduler.IScheduler;
import io.github.kol.oss.taskforge.core.status.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.IStatus;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

import java.util.concurrent.TimeUnit;

public class Task<T> implements ITask<T> {
    protected volatile IDescriptors<T> descriptors;
    protected volatile IStateExecutor executor;

    public Task(final IDescriptors<T> descriptors, IStateExecutor executor) {
        this.descriptors = descriptors;
        this.executor = executor;
    }

    @Override
    public void start() {
        this.executor.execute(this.descriptors);
    }

    @Override
    public void cancel() {
        ICancelToken token = this.descriptors.getCancelToken();
        if (token.isCancelled()) {
            throw new IllegalStateException("Task is already cancelled");
        }

        token.cancel();
    }

    @Override
    public void await() throws InterruptedException {
        this.descriptors.getStatus().getFinishedEvent().await();
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.descriptors.getStatus().getFinishedEvent().await(timeout, unit);
    }

    @Override
    public TaskState getState() {
        return this.descriptors.getStatus().getState();
    }

    @Override
    public T getResult() throws Exception {
        this.descriptors.getStatus().getFinishedEvent().await();

        TaskState state = this.descriptors.getStatus().getState();
        if (state == TaskState.FAILED || state == TaskState.CANCELED) {
            throw this.descriptors.getException();
        }

        return this.descriptors.getResult();
    }

    @Override
    public Exception getException() {
        return this.descriptors.getException();
    }

    public IAction<T> getAction() {
        return this.descriptors.getAction();
    }

    public IScheduler getScheduler() {
        return this.descriptors.getScheduler();
    }

    public IStatus getStatus() {
        return this.descriptors.getStatus();
    }

    public <N> ITask<N> then(ITask<N> task) {
        this.descriptors.getStatus().getFinishedEvent().addListener(task::start);
        return task;
    }
}
