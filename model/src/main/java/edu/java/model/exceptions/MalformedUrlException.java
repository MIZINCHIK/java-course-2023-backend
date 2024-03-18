package edu.java.model.exceptions;

public class MalformedUrlException extends IllegalStateException {
    private static final String MESSAGE = "URL provided isn't supported";

    public MalformedUrlException() {
        super(MESSAGE);
    }

    public MalformedUrlException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
