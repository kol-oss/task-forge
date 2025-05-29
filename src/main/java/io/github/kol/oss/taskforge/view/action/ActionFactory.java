package io.github.kol.oss.taskforge.view.action;

import io.github.kol.oss.taskforge.core.action.IAction;
import io.github.kol.oss.taskforge.core.action.IEmptyAction;
import io.github.kol.oss.taskforge.core.action.IEmptyVoidAction;
import io.github.kol.oss.taskforge.core.action.IVoidAction;
import io.github.kol.oss.taskforge.service.action.EmptyAction;
import io.github.kol.oss.taskforge.service.action.EmptyVoidAction;
import io.github.kol.oss.taskforge.service.action.VoidAction;

public class ActionFactory {
    public static <T> IAction<T> create(IEmptyAction<T> action) {
        return new EmptyAction<>(action);
    }

    public static IAction<Void> create(IVoidAction action) {
        return new VoidAction(action);
    }

    public static IAction<Void> create(IEmptyVoidAction action) {
        return new EmptyVoidAction(action);
    }
}