package edu.java.scrapper.exceptions;

public class LinkNotTrackedException extends IllegalStateException {
    private static final String MESSAGE = "Link isn't being tracked at the moment";

    public LinkNotTrackedException() {
        super(MESSAGE);
    }
}
