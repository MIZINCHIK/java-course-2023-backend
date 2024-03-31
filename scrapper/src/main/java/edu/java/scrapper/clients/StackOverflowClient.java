package edu.java.scrapper.clients;

import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowAnswer;
import edu.java.scrapper.clients.updates.stackoverflow.StackOverflowUpdate;
import edu.java.scrapper.clients.updates.stackoverflow.auxiliary.StackOverflowAnswers;
import edu.java.scrapper.clients.updates.stackoverflow.auxiliary.StackOverflowQuestions;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import static edu.java.scrapper.clients.ClientsUtils.getFirstInSingleElementList;
import static edu.java.scrapper.clients.ClientsUtils.sendStackoverflowRequest;

public interface StackOverflowClient {
    String SITE_PARAMETER = "stackoverflow.com";

    @GetExchange("/questions/{ids}?site=" + SITE_PARAMETER)
    StackOverflowQuestions getQuestionData(@PathVariable String ids);

    @GetExchange("/questions/{ids}/answers?site=" + SITE_PARAMETER)
    StackOverflowAnswers getAnswers(@PathVariable String ids);

    default StackOverflowUpdate getUpdate(String id) {
        return getFirstInSingleElementList(getQuestionData(id).items());
    }

    default List<StackOverflowAnswer> getAnswerList(String id) {
        return getAnswers(id).items();
    }

    default StackOverflowUpdate getUpdateByUrl(String url) throws MalformedUrlException {
        return sendStackoverflowRequest(url, this::getUpdate);
    }

    default List<StackOverflowAnswer> getAnswerListByUrl(String url) throws MalformedUrlException {
        return sendStackoverflowRequest(url, this::getAnswerList);
    }
}
