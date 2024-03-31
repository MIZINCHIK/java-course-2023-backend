package edu.java.scrapper.clients;

import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.clients.updates.StackOverflowResponse;
import edu.java.scrapper.clients.updates.StackOverflowUpdate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {
    String INCORRECT_ID = "Incorrect id";
    String SITE_PARAMETER = "stackoverflow.com";
    Pattern PATTERN = Pattern.compile("^.*\\/stackoverflow\\.com\\/questions\\/(\\d*)\\/.*$");

    @GetExchange("/questions/{ids}?site=" + SITE_PARAMETER)
    StackOverflowResponse getResponse(@PathVariable String ids);

    default StackOverflowUpdate getUpdate(String id) {
        List<StackOverflowUpdate> updates = getResponse(id).items();
        if (updates.size() != 1) {
            throw new IllegalStateException(INCORRECT_ID);
        }
        return updates.getFirst();
    }

    default StackOverflowUpdate getUpdateByUrl(String url) {
        Matcher matcher = PATTERN.matcher(url);
        if (matcher.find()) {
            return getUpdate(matcher.group(1));
        } else {
            throw new MalformedUrlException();
        }
    }
}
