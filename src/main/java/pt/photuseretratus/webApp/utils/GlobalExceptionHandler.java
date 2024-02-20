package pt.photuseretratus.webApp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.photuseretratus.webApp.exceptions.AuthenticationLevelException;
import pt.photuseretratus.webApp.exceptions.EmailLevelException;
import pt.photuseretratus.webApp.exceptions.ImageKitApiLevelException;
import pt.photuseretratus.webApp.exceptions.SecurityLevelException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityLevelException.class)
    ResponseEntity<String> handleException(SecurityLevelException exception) {
        log.error("", exception);
        return new ResponseEntity<>(exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ImageKitApiLevelException.class)
    ResponseEntity<String> handleException(ImageKitApiLevelException exception) {
        log.error("", exception);
        return new ResponseEntity<>(exception.getLocalizedMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationLevelException.class)
    ResponseEntity<String> handleException(AuthenticationLevelException exception) {
        log.error("", exception);
        return new ResponseEntity<>(exception.getMessage(),
                exception.getStatus());
    }

    @ExceptionHandler(EmailLevelException.class)
    ResponseEntity<String> handleException(EmailLevelException exception) {
        log.error("", exception);
        return new ResponseEntity<>(exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}