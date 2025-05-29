package io.github.kol.oss.taskforge.core.status;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public interface IStatus {
    TaskState getState();

    void setState(TaskState state);

    IEvent getEvent(TaskState state);

    IEvent getFinishedEvent();
}
