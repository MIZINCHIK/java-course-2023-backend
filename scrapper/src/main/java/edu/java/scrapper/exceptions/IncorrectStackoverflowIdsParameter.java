package edu.java.scrapper.exceptions;

public class IncorrectStackoverflowIdsParameter extends IllegalStateException {
    private static final String MESSAGE = "Only single id is supported in this request";

    public IncorrectStackoverflowIdsParameter() {
        super(MESSAGE);
    }
}
