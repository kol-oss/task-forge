package io.github.kol.oss.taskforge.core.action;

import io.github.kol.oss.taskforge.core.cancel.ICancelToken;

@FunctionalInterface
public interface IVoidAction {
    void run(ICancelToken token) throws Exception;
}
