package io.github.kol.oss.taskforge.service.status.state;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.core.descriptors.IDescriptors;
import io.github.kol.oss.taskforge.core.status.state.IStateHandler;
import io.github.kol.oss.taskforge.core.status.state.TaskState;

public class RunningStateHandler extends BasicStateHandler {
    protected volatile IStateHandler completedExecutor;
    protected volatile IStateHandler failedExecutor;
    protected volatile IStateHandler canceledExecutor;

    public RunningStateHandler(IStateHandler completedExecutor, IStateHandler failedExecutor, IStateHandler canceledExecutor) {
        super(TaskState.RUNNING);

        this.completedExecutor = completedExecutor;
        this.failedExecutor = failedExecutor;
        this.canceledExecutor = canceledExecutor;
    }

    @Override
    public <T> void handle(IDescriptors<T> descriptors) {
        super.handle(descriptors);

        ICancelToken token = descriptors.getCancelToken();

        try {
            token.throwIfCancelled();

            T value = descriptors.getAction().run(token);
            descriptors.setResult(value);
            this.completedExecutor.handle(descriptors);
        } catch (Exception exception) {
            descriptors.setException(exception);

            if (exception instanceof CancelException) {
                this.canceledExecutor.handle(descriptors);
            } else {
                this.failedExecutor.handle(descriptors);
            }
        }
    }
}
