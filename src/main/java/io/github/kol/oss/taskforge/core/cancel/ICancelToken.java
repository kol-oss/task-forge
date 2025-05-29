package io.github.kol.oss.taskforge.core.cancel;

public interface ICancelToken {
    void cancel();

    boolean isCancelled();

    void throwIfCancelled() throws CancelException;
}
