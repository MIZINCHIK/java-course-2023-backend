package edu.java.scrapper.exceptions;

public class UserAlreadyRegisteredException extends IllegalStateException {
    private static final String MESSAGE = "Sought user is already registered";

    public UserAlreadyRegisteredException() {
        super(MESSAGE);
    }
}
