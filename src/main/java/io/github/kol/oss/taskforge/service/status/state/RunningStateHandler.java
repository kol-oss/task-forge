package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class RunningStateHandler extends BasicStateHandler {
    protected volatile IStateHandler completedHandler;
    protected volatile IStateHandler failedHandler;
    protected volatile IStateHandler canceledHandler;

    public RunningStateHandler(IStateHandler completedHandler, IStateHandler failedHandler, IStateHandler canceledHandler) {
        super(TaskState.RUNNING);

        this.completedHandler = completedHandler;
        this.failedHandler = failedHandler;
        this.canceledHandler = canceledHandler;
    }

    @Override
    public <T> void handle(IDescriptors<T> descriptors) {
        super.handle(descriptors);

        ICancelToken token = descriptors.getCancelToken();

        try {
            token.throwIfCancelled();

            T value = descriptors.getAction().run(token);
            descriptors.setResult(value);
            this.completedHandler.handle(descriptors);
        } catch (Exception exception) {
            descriptors.setException(exception);

            if (exception instanceof CancelException) {
                this.canceledHandler.handle(descriptors);
            } else {
                this.failedHandler.handle(descriptors);
            }
        }
    }
}
