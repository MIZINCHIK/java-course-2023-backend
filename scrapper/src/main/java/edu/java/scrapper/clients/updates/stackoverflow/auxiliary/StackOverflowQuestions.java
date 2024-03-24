package edu.java.scrapper.clients.updates.stackoverflow.auxiliary;

import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowUpdate;
import java.util.List;

public record StackOverflowQuestions(List<StackOverflowUpdate> items) {
}
