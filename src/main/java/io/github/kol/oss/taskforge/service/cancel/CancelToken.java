package io.github.kol.oss.taskforge.service.cancel;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;

import java.util.concurrent.atomic.AtomicBoolean;

public class CancelToken implements ICancelToken {
    protected volatile AtomicBoolean cancelled = new AtomicBoolean(false);

    @Override
    public void cancel() {
        this.cancelled.set(true);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled.get();
    }

    @Override
    public void throwIfCancelled() throws CancelException {
        if (this.cancelled.get()) {
            throw new CancelException();
        }
    }
}
