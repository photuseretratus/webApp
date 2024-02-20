package pt.photuseretratus.webApp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationLevelException extends RuntimeException {

    private final HttpStatus status;

    public AuthenticationLevelException(final String message, final HttpStatus status, final Throwable throwable) {
        super(message, throwable);
        this.status = status;
    }

    public AuthenticationLevelException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

}
