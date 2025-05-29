package io.github.kol.oss.taskforge.core.cancel;

public class CancelException extends Exception {
    public CancelException() {
        super("Task was cancelled");
    }
}
