package pt.photuseretratus.webApp.exceptions;

public class SecurityLevelException extends RuntimeException {

    public SecurityLevelException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
