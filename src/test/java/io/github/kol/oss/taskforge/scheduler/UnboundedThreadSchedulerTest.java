package io.github.kol.oss.taskforge.scheduler;

import io.github.kol.oss.taskforge.service.scheduler.UnboundedThreadScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unbounded Thread Scheduler Tests")
class UnboundedThreadSchedulerTest {
    private static long distinctThreadCount(long[] threadIds) {
        return java.util.Arrays.stream(threadIds).distinct().count();
    }

    @Test
    @DisplayName("schedule should execute action in new thread")
    @Timeout(1)
    void schedule_shouldExecuteActionInNewThread() throws InterruptedException {
        // Arrange
        UnboundedThreadScheduler scheduler = new UnboundedThreadScheduler();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean executed = new AtomicBoolean(false);
        long mainThreadId = Thread.currentThread().threadId();

        // Act
        scheduler.schedule(() -> {
            executed.set(true);
            assertNotEquals(mainThreadId, Thread.currentThread().threadId());
            latch.countDown();
        });

        // Assert
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        assertTrue(executed.get());
    }

    @Test
    @DisplayName("schedule should execute multiple actions in parallel")
    @Timeout(2)
    void schedule_shouldExecuteMultipleActionsInParallel() throws InterruptedException {
        // Arrange
        UnboundedThreadScheduler scheduler = new UnboundedThreadScheduler();
        int numTasks = 3;
        CountDownLatch startLatch = new CountDownLatch(numTasks);
        CountDownLatch finishLatch = new CountDownLatch(numTasks);
        long[] threadIds = new long[numTasks];

        // Act
        for (int i = 0; i < numTasks; i++) {
            final int index = i;
            scheduler.schedule(() -> {
                threadIds[index] = Thread.currentThread().threadId();
                startLatch.countDown();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finishLatch.countDown();
            });
        }

        // Assert
        assertTrue(startLatch.await(500, TimeUnit.MILLISECONDS));
        assertTrue(finishLatch.await(1, TimeUnit.SECONDS));

        assertEquals(numTasks, distinctThreadCount(threadIds));
    }

    @Test
    @DisplayName("close should not prevent new tasks from being scheduled")
    @Timeout(1)
    void close_shouldNotAffectScheduling() throws InterruptedException {
        // Arrange
        UnboundedThreadScheduler scheduler = new UnboundedThreadScheduler();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean executed = new AtomicBoolean(false);

        // Act
        scheduler.close();
        scheduler.schedule(() -> {
            executed.set(true);
            latch.countDown();
        });

        // Assert
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        assertTrue(executed.get());
    }

    @Test
    @DisplayName("schedule should handle null action")
    @Timeout(1)
    void schedule_shouldHandleNullAction() {
        // Arrange
        UnboundedThreadScheduler scheduler = new UnboundedThreadScheduler();

        // Act & Assert
        assertDoesNotThrow(() -> scheduler.schedule(null));
    }
}