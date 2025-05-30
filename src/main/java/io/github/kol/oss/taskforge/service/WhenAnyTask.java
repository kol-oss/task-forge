package io.github.kol.oss.taskforge.service;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.service.status.WhenAnyStateExecutor;

import java.util.Collection;

public class WhenAnyTask<T> extends Task<ITask<T>> {
    protected volatile Collection<Task<T>> tasks;

    public WhenAnyTask(IDescriptors<ITask<T>> descriptors, Collection<ITask<T>> tasks) {
        super(descriptors, new WhenAnyStateExecutor<>(tasks));
    }

    public Collection<Task<T>> getTasks() {
        return this.tasks;
    }
}