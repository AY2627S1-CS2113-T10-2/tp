package jobseek.exception;

/**
 * Signals an error during command execution or input parsing within the JobSeek application.
 */
public class JobSeekException extends Exception {

    /**
     * Constructs a JobSeekException with the specified detail message.
     *
     * @param message Detail message describing the error.
     */
    public JobSeekException(String message) {
        super(message);
    }

    /**
     * Constructs a JobSeekException with the specified detail message and cause.
     *
     * @param message Detail message describing the error.
     * @param cause Underlying exception that caused this error.
     */
    public JobSeekException(String message, Throwable cause) {
        super(message, cause);
    }
}
