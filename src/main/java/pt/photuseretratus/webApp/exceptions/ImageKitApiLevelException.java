package pt.photuseretratus.webApp.exceptions;

public class ImageKitApiLevelException extends RuntimeException {

    public ImageKitApiLevelException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public ImageKitApiLevelException(final String message) {
        super(message);
    }

}
