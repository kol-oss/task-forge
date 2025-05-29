package io.github.kol.oss.taskforge.core.status.state;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;

public interface IStateExecutor {
    TaskState getState();

    <T> void execute(IDescriptors<T> descriptors);
}
