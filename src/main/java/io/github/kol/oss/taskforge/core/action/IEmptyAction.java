package io.github.kol.oss.taskforge.core.action;

@FunctionalInterface
public interface IEmptyAction<T> {
    T run() throws Exception;
}
