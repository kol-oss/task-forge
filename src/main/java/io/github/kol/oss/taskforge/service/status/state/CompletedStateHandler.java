package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class CompletedStateHandler extends BasicStateHandler {
    public CompletedStateHandler() {
        super(TaskState.COMPLETED);
    }
}
