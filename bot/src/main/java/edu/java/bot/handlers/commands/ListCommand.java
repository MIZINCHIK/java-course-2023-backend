package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import edu.java.bot.links.Link;
import java.net.URL;
import java.util.List;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;

public class ListCommand extends Command {
    private static final String NAME = "list";
    private static final String DESCRIPTION = "List the links being tracked. No arguments.";
    private static final String EMPTY_TRACKING_LIST = "No links being tracked.";

    public ListCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void handle(Message message, String[] args, PrimaveraBot bot) {
        if (!bot.isUserRegistered(message.from().id())) {
            handleUserNotRegistered(message, bot);
        } else {
            List<URL> tracked = bot.getLinksByUserId(message.from().id());
            if (tracked.isEmpty()) {
                bot.respond(message.chat().id(), message.messageId(), EMPTY_TRACKING_LIST);
            } else {
                Table.Builder tableBuilder = new Table.Builder()
                    .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
                    .addRow("Website", "Link");
                tracked.stream()
                    .map(link -> new Link(link.toString()))
                    .forEach(link -> tableBuilder.addRow(link.getDomain(), link.getUrl().toString()));
                bot.respondMd(message.chat().id(), message.messageId(),
                    new CodeBlock(tableBuilder.build().toString(), "Tracked links").toString()
                );
            }
        }
    }
}
