package edu.java.bot.handlers;

import com.pengrad.telegrambot.model.BotCommand;
import edu.java.bot.links.Link;
import java.net.URL;
import java.util.List;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import static edu.java.bot.links.Link.isLinkCorrect;

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

    public static String buildLinks(List<URL> tracked) {
        Table.Builder tableBuilder = new Table.Builder()
            .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
            .addRow("Website", "Link");
        tracked.stream()
            .filter(link -> isLinkCorrect(link.toString()))
            .map(link -> new Link(link.toString()))
            .forEach(link -> tableBuilder.addRow(link.getDomain(), link.getUrl().toString()));
        return new CodeBlock(tableBuilder.build().toString(), "Tracked links").toString();
    }
}
