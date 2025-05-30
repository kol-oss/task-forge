package io.github.kol.oss.taskforge.cancel;

import io.github.kol.oss.taskforge.core.cancel.CancelException;
import io.github.kol.oss.taskforge.core.cancel.ICancelToken;
import io.github.kol.oss.taskforge.service.cancel.CancelToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cancel Token Tests")
class CancelTokenTest {
    @Test
    @DisplayName("Initial state should not be cancelled")
    void initialState_shouldNotBeCancelled() {
        // Arrange & Act
        ICancelToken token = new CancelToken();

        // Assert
        assertFalse(token.isCancelled());
    }

    @Test
    @DisplayName("cancel should set token to cancelled state")
    void cancel_shouldSetCancelledState() {
        // Arrange
        ICancelToken token = new CancelToken();

        // Act
        token.cancel();

        // Assert
        assertTrue(token.isCancelled());
    }

    @Test
    @DisplayName("throwIfCancelled should not throw when not cancelled")
    void throwIfCancelled_shouldNotThrow_whenNotCancelled() {
        // Arrange
        ICancelToken token = new CancelToken();

        // Act & Assert
        assertDoesNotThrow(token::throwIfCancelled);
    }

    @Test
    @DisplayName("throwIfCancelled should throw CancelException when cancelled")
    void throwIfCancelled_shouldThrow_whenCancelled() {
        // Arrange
        ICancelToken token = new CancelToken();
        token.cancel();

        // Act & Assert
        assertThrows(CancelException.class, token::throwIfCancelled);
    }

    @Test
    @DisplayName("Multiple cancels should keep token in cancelled state")
    void cancel_shouldKeepTokenCancelled_whenCalledMultipleTimes() {
        // Arrange
        ICancelToken token = new CancelToken();

        // Act
        token.cancel();
        token.cancel();
        token.cancel();

        // Assert
        assertTrue(token.isCancelled());
    }

    @Test
    @DisplayName("isCancelled should reflect current cancellation state")
    void isCancelled_shouldReflectCurrentState() {
        // Arrange
        ICancelToken token = new CancelToken();

        // Act & Assert before cancel
        assertFalse(token.isCancelled());

        // Act & Assert after cancel
        token.cancel();
        assertTrue(token.isCancelled());
    }
}