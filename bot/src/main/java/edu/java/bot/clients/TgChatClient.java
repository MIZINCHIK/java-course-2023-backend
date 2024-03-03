package edu.java.bot.clients;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(value = "/tg-chat")
public interface TgChatClient {
    @GetExchange("/{id}")
    void getChat(@PathVariable Long id);

    @PostExchange("/{id}")
    void postChat(@PathVariable Long id);

    @DeleteExchange("/{id}")
    void deleteChat(@PathVariable Long id);
}
