package edu.java.bot.handlers.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import edu.java.bot.PrimaveraBot;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;

public class HelpCommand extends Command {
    public static final String NAME = "/help";
    private static final String DESCRIPTION = "Show commands. No arguments.";

    public HelpCommand() {
        super(NAME, DESCRIPTION);
    }

    private String formCommands(PrimaveraBot bot) {
        BotCommand[] commands = bot.getCommands();
        Table.Builder tableBuilder = new Table.Builder()
            .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
            .addRow("Command", "Description");
        for (BotCommand command : commands) {
            tableBuilder.addRow(command.command(), command.description());
        }
        return new CodeBlock(tableBuilder.build().toString(), "Commands").toString();
    }

    public void handle(Message message, String[] args, PrimaveraBot bot) {
        String commands = formCommands(bot);
        bot.respondMd(message.chat().id(), message.messageId(), commands);
    }
}
