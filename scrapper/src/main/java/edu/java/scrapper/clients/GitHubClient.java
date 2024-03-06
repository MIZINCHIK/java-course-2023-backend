package edu.java.scrapper.clients;

import edu.java.scrapper.clients.updates.GithubUpdate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GitHubClient {
    @GetExchange("/repos/{owner}/{repo}")
    GithubUpdate getUpdate(@PathVariable String owner, @PathVariable String repo);
}
