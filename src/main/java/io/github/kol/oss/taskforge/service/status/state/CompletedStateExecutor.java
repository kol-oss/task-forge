package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class CompletedStateExecutor extends BasicStateExecutor {
    public CompletedStateExecutor() {
        super(TaskState.COMPLETED);
    }
}
