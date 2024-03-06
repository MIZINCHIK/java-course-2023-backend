package edu.java.scrapper.clients;

import edu.java.scrapper.clients.updates.StackOverflowResponse;
import edu.java.scrapper.clients.updates.StackOverflowUpdate;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {
    String INCORRECT_ID = "Incorrect id";
    String SITE_PARAMETER = "stackoverflow.com";

    @GetExchange("/questions/{ids}?site=" + SITE_PARAMETER)
    StackOverflowResponse getResponse(@PathVariable String ids);

    default StackOverflowUpdate getUpdate(String id) {
        List<StackOverflowUpdate> updates = getResponse(id).items();
        if (updates.size() != 1) {
            throw new IllegalStateException(INCORRECT_ID);
        }
        return updates.getFirst();
    }
}
