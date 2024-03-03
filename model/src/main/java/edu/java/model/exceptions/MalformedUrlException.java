package edu.java.model.exceptions;

public class MalformedUrlException extends IllegalStateException {
    private static final String MESSAGE = "URL provided isn't full or is malformed";

    public MalformedUrlException() {
        super(MESSAGE);
    }
}
