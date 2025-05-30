package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class BasicStateHandler implements IStateHandler {
    protected volatile TaskState state;

    public BasicStateHandler(TaskState state) {
        this.state = state;
    }

    @Override
    public TaskState getState() {
        return this.state;
    }

    @Override
    public <T> void handle(IDescriptors<T> descriptors) {
        descriptors.getStatus().setState(this.state);
    }
}
