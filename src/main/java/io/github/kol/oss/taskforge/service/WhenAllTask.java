package io.github.kol.oss.taskforge.service;

import io.github.kol.oss.taskforge.core.ITask;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.service.status.WhenAllStateExecutor;

import java.util.Collection;

public class WhenAllTask<T> extends Task<Collection<T>> {
    protected volatile Collection<Task<T>> tasks;

    public WhenAllTask(IDescriptors<Collection<T>> descriptors, Collection<ITask<T>> tasks) {
        super(descriptors, new WhenAllStateExecutor<>(tasks));
    }

    public Collection<Task<T>> getTasks() {
        return this.tasks;
    }
}
