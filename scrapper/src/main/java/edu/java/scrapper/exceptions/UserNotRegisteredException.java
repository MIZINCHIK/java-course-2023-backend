package edu.java.scrapper.exceptions;

public class UserNotRegisteredException extends IllegalStateException {
    private static final String MESSAGE = "Sought user isn't registered";

    public UserNotRegisteredException() {
        super(MESSAGE);
    }
}
