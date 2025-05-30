package io.github.kol.oss.taskforge.status;

import io.github.kol.oss.taskforge.core.status.IEvent;
import io.github.kol.oss.taskforge.service.status.StateEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("State Event Tests")
public class StateEventTest {
    @Test
    @DisplayName("hasAlerted should return false initially")
    void hasAlerted_shouldReturnFalse_whenInitialized() {
        // Arrange
        IEvent event = new StateEvent();

        // Act & Assert
        assertFalse(event.hasAlerted());
    }

    @Test
    @DisplayName("alert should change hasAlerted to true")
    void alert_shouldSetHasAlertedToTrue_whenCalled() {
        // Arrange
        IEvent event = new StateEvent();

        // Act
        event.alert();

        // Assert
        assertTrue(event.hasAlerted());
    }

    @Test
    @DisplayName("alert should notify waiting threads")
    @Timeout(1)
    void alert_shouldNotifyWaitingThreads_whenCalled() throws InterruptedException {
        // Arrange
        IEvent event = new StateEvent();
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                event.await();
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        Thread.sleep(100);

        // Act
        event.alert();

        // Assert
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("await should return immediately when already alerted")
    @Timeout(1)
    void await_shouldReturnImmediately_whenAlreadyAlerted() throws InterruptedException {
        // Arrange
        IEvent event = new StateEvent();
        event.alert();

        // Act & Assert (should not block)
        event.await();
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 50, 100})
    @DisplayName("await with timeout should return true when already alerted")
    @Timeout(1)
    void awaitWithTimeout_shouldReturnTrue_whenAlreadyAlerted(long timeout) throws InterruptedException {
        // Arrange
        IEvent event = new StateEvent();
        event.alert();

        // Act & Assert
        assertTrue(event.await(timeout, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("await with timeout should return false when not alerted")
    @Timeout(1)
    void awaitWithTimeout_shouldReturnFalse_whenNotAlerted() throws InterruptedException {
        // Arrange
        IEvent event = new StateEvent();

        // Act & Assert
        assertFalse(event.await(50, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("addListener should immediately run listener when already alerted")
    void addListener_shouldRunListenerImmediately_whenAlreadyAlerted() {
        // Arrange
        IEvent event = new StateEvent();
        event.alert();
        AtomicInteger counter = new AtomicInteger(0);

        // Act
        event.addListener(counter::incrementAndGet);

        // Assert
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("addListener should not run listener immediately when not alerted")
    void addListener_shouldNotRunListenerImmediately_whenNotAlerted() {
        // Arrange
        IEvent event = new StateEvent();
        AtomicInteger counter = new AtomicInteger(0);

        // Act
        event.addListener(counter::incrementAndGet);

        // Assert
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("alert should run all listeners")
    void alert_shouldRunAllListeners_whenCalled() {
        // Arrange
        IEvent event = new StateEvent();
        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);

        event.addListener(counter1::incrementAndGet);
        event.addListener(counter2::incrementAndGet);

        // Act
        event.alert();

        // Assert
        assertEquals(1, counter1.get());
        assertEquals(1, counter2.get());
    }

    @Test
    @DisplayName("removeListener should prevent listener from being called")
    void removeListener_shouldPreventListenerFromBeingCalled() {
        // Arrange
        IEvent event = new StateEvent();
        AtomicInteger counter = new AtomicInteger(0);
        Runnable listener = counter::incrementAndGet;

        event.addListener(listener);
        event.removeListener(listener);

        // Act
        event.alert();

        // Assert
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("await with timeout should throw IllegalArgumentException when TimeUnit is null")
    void awaitWithTimeout_shouldThrowException_whenTimeUnitIsNull() {
        // Arrange
        IEvent event = new StateEvent();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> event.await(100, null));
    }
}