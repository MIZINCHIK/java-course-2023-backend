package edu.java.scrapper.updates;

import edu.java.scrapper.clients.updates.github.Commit;
import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowAnswer;
import java.time.OffsetDateTime;
import static java.lang.System.lineSeparator;

public class UpdateDescriptionFormatter {
    private UpdateDescriptionFormatter() {
        throw new IllegalStateException();
    }

    public static String formatNewCommitMessage(Commit commit) {
        return "There is a new commit to the repository tracked: " +
                lineSeparator() +
                "Commit with the following url: " +
                commit.url() +
                lineSeparator() +
                "Author name: " +
                commit.info().author().name() +
                lineSeparator() +
                "Commit date: " +
                commit.info().author().date() +
                lineSeparator();
    }

    public static String formatNewAnswerMessage(StackOverflowAnswer answer) {
        return "There is a new answer to the question tracked: " +
                "https://stackoverflow.com/a/" +
                answer.answerId() +
                lineSeparator() +
                "Answered by user with name: " +
                answer.owner().name() +
                lineSeparator() +
                "And id: " +
                answer.owner().userId() +
                lineSeparator() +
                "And url: " +
                answer.owner().link() +
                lineSeparator() +
                "At the following date and time: " +
                answer.creationDate() +
                lineSeparator();
    }

    public static String formatNewUpdateMessage(OffsetDateTime date) {
        return "Last updated at: " + date;
    }
}
