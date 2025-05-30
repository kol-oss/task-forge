package io.github.kol.oss.taskforge.core.status.state;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;

public interface IStateHandler {
    TaskState getState();

    <T> void handle(IDescriptors<T> descriptors);
}
