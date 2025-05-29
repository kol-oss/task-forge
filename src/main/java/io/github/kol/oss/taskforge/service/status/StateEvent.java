package io.github.kol.oss.taskforge.service.status;

import io.github.kol.oss.taskforge.core.status.IEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateEvent implements IEvent {
    protected final List<Runnable> listeners = new LinkedList<>();
    protected final AtomicBoolean alerted = new AtomicBoolean(false);

    @Override
    public synchronized void await() throws InterruptedException {
        if (!this.alerted.get())
            this.wait();
    }

    @Override
    public synchronized boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null)
            throw new IllegalArgumentException("TimeUnit must not be null");

        if (!this.alerted.get())
            this.wait(unit.toMillis(timeout));

        return this.alerted.get();
    }

    @Override
    public synchronized void alert() {
        if (this.alerted.compareAndSet(false, true)) {
            this.notifyAll();

            this.listeners.forEach(Runnable::run);
        }
    }

    @Override
    public boolean hasAlerted() {
        return this.alerted.get();
    }

    @Override
    public void addListener(Runnable listener) {
        this.listeners.add(listener);

        if (this.alerted.get()) {
            listener.run();
        }
    }

    @Override
    public void removeListener(Runnable listener) {
        this.listeners.remove(listener);
    }
}
