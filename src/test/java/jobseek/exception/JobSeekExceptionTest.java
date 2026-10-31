package jobseek.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link JobSeekException}.
 */
public class JobSeekExceptionTest {

    @Test
    public void constructor_withMessageOnly_setsMessageAndNullCause() {
        JobSeekException exception = new JobSeekException("Test error message");
        assertEquals("Test error message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void constructor_withMessageAndCause_chainsUnderlyingThrowable() {
        IllegalArgumentException cause = new IllegalArgumentException("Invalid argument");
        JobSeekException exception = new JobSeekException("Wrapped error", cause);

        assertEquals("Wrapped error", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
