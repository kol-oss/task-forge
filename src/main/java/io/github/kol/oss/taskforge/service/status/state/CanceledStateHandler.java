package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class CanceledStateHandler extends BasicStateHandler {
    public CanceledStateHandler() {
        super(TaskState.CANCELED);
    }
}
