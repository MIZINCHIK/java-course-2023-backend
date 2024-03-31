package edu.java.scrapper.clients;

import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.clients.updates.GithubUpdate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GitHubClient {
    Pattern PATTERN = Pattern.compile("^.*\\/github\\.com\\/([^\\/]*)\\/([^\\/]*)(\\/.*)*$");

    @GetExchange("/repos/{owner}/{repo}")
    GithubUpdate getUpdate(@PathVariable String owner, @PathVariable String repo);

    default GithubUpdate getUpdate(String url) {
        Matcher matcher = PATTERN.matcher(url);
        if (matcher.find()) {
            return getUpdate(matcher.group(1), matcher.group(2));
        } else {
            throw new MalformedUrlException();
        }
    }
}
