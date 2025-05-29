package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateExecutor;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class BasicStateExecutor implements IStateExecutor {
    protected volatile TaskState state;

    public BasicStateExecutor(TaskState state) {
        this.state = state;
    }

    @Override
    public TaskState getState() {
        return this.state;
    }

    @Override
    public <T> void execute(IDescriptors<T> descriptors) {
        descriptors.getStatus().setState(this.state);
    }
}
