package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.BotCommand;
import edu.java.model.dto.LinkUpdate;
import edu.java.model.links.Link;
import java.net.URI;
import java.util.List;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import static edu.java.model.links.Link.isLinkCorrect;
import static java.lang.System.lineSeparator;

public class MessageFormatter {
    private MessageFormatter() {
        throw new IllegalStateException();
    }

    public static String buildCommands(BotCommand[] commands) {
        Table.Builder tableBuilder = new Table.Builder()
            .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
            .addRow("Command", "Description");
        for (BotCommand command : commands) {
            tableBuilder.addRow(command.command(), command.description());
        }
        return new CodeBlock(tableBuilder.build().toString(), "Commands").toString();
    }

    public static String buildLinks(List<URI> tracked) {
        Table.Builder tableBuilder = new Table.Builder()
            .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
            .addRow("Website", "Link");
        tracked.stream()
            .filter(link -> isLinkCorrect(link.toString()))
            .map(link -> new Link(link.toString()))
            .forEach(link -> tableBuilder.addRow(link.getDomain(), link.getUrl().toString()));
        return new CodeBlock(tableBuilder.build().toString(), "Tracked links").toString();
    }

    public static String formUpdateMessage(LinkUpdate update) {
        return "The following URL has been updated: " +
                lineSeparator() +
                update.url() +
                lineSeparator() +
                "With description: " +
                lineSeparator() +
                update.description() +
                lineSeparator();
    }
}
