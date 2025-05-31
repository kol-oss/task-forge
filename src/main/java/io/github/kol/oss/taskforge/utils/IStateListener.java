package io.github.kol.oss.taskforge.utils;

import io.github.kol.oss.taskforge.core.status.state.TaskState;

public interface IStateListener {
    void onScheduled();

    void onRunning();

    void onCanceled();

    void onFailed();

    void onCompleted();

    void onFinished();

    default Runnable stateToFunction(TaskState state) {
        return switch (state) {
            case SCHEDULED -> this::onScheduled;
            case RUNNING -> this::onRunning;
            case COMPLETED -> this::onCompleted;
            case CANCELED -> this::onCanceled;
            default -> this::onFailed;
        };
    }
}
