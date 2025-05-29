package io.github.kol.oss.taskforge.core.status;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;

public interface ITaskStateExecutor {
    <T> void execute(IDescriptors<T> descriptors);
}
