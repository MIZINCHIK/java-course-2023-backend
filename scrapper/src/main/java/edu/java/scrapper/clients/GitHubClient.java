package edu.java.scrapper.clients;

import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.clients.updates.github.Commit;
import edu.java.scrapper.clients.updates.github.GithubUpdate;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import static edu.java.scrapper.clients.ClientsUtils.sendGithubRequest;

public interface GitHubClient {

    @GetExchange("/repos/{owner}/{repo}")
    GithubUpdate getUpdate(@PathVariable String owner, @PathVariable String repo);

    @GetExchange("/repos/{owner}/{repo}/commits")
    List<Commit> getCommits(@PathVariable String owner, @PathVariable String repo);

    default GithubUpdate getUpdate(String url) throws MalformedUrlException {
        return sendGithubRequest(url, this::getUpdate);
    }

    default List<Commit> getCommits(String url) throws MalformedUrlException {
        return sendGithubRequest(url, this::getCommits);
    }
}
