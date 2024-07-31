package pt.photuseretratus.webApp.exceptions;

import lombok.Getter;

@Getter
public class EmailLevelException extends RuntimeException {

    public EmailLevelException(final String message, final Throwable throwable) {
        super(message, throwable);
    }


}
