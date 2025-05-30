package io.github.kol.oss.taskforge.scheduler;

import io.github.kol.oss.taskforge.service.scheduler.BoundedThreadScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bounded Thread Scheduler Tests")
class BoundedThreadSchedulerTest {
    @Test
    @DisplayName("Default constructor should use available processors")
    void constructor_shouldUseAvailableProcessors_whenDefaultConstructorUsed() {
        // Arrange & Act
        BoundedThreadScheduler scheduler = new BoundedThreadScheduler();

        try {
            // Assert
            assertEquals(Runtime.getRuntime().availableProcessors(), scheduler.getConcurrencyLevel());
        } finally {
            scheduler.close();
        }
    }

    @Test
    @DisplayName("Constructor should set specified concurrency level")
    void constructor_shouldSetConcurrencyLevel_whenSpecified() {
        // Arrange & Act
        int expectedLevel = 4;
        BoundedThreadScheduler scheduler = new BoundedThreadScheduler(expectedLevel);

        try {
            // Assert
            assertEquals(expectedLevel, scheduler.getConcurrencyLevel());
        } finally {
            scheduler.close();
        }
    }

    @Test
    @DisplayName("schedule should execute submitted action")
    @Timeout(1)
    void schedule_shouldExecuteAction_whenSubmitted() throws InterruptedException {
        // Arrange
        BoundedThreadScheduler scheduler = new BoundedThreadScheduler(2);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean executed = new AtomicBoolean(false);

        try {
            // Act
            scheduler.schedule(() -> {
                executed.set(true);
                latch.countDown();
            });

            // Assert
            assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
            assertTrue(executed.get());
        } finally {
            scheduler.close();
        }
    }

    @Test
    @DisplayName("close should shutdown thread pool")
    @Timeout(1)
    void close_shouldShutdownPool_whenCalled() throws InterruptedException {
        // Arrange
        BoundedThreadScheduler scheduler = new BoundedThreadScheduler(2);
        AtomicBoolean executedAfterClose = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // Act
        scheduler.close();

        try {
            scheduler.schedule(() -> {
                executedAfterClose.set(true);
                latch.countDown();
            });
        } catch (Exception ignored) {
        }

        // Assert
        assertFalse(latch.await(200, TimeUnit.MILLISECONDS));
        assertFalse(executedAfterClose.get());
    }
}