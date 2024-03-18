package edu.java.scrapper.clients;

import edu.java.model.exceptions.MalformedUrlException;
import edu.java.scrapper.exceptions.IncorrectStackoverflowIdsParameter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientsUtils {
    private static final Pattern GITHUB_PATTERN = Pattern.compile("^(?:https:\\/\\/)?(?:www\\.)?github\\.com\\/([^\\/]*)\\/([^\\/]*)(\\/.*)*$");
    private static final Pattern STACKOVERFLOW_PATTERN = Pattern.compile("^(?:https:\\/\\/)?(?:www\\.)?stackoverflow\\.com\\/questions\\/(\\d*)(\\/.*)*$");

    private ClientsUtils() {
        throw new IllegalStateException();
    }

    public static <T> T sendStackoverflowRequest(String url, Function<String, T> requester) throws MalformedUrlException {
        Matcher matcher = STACKOVERFLOW_PATTERN.matcher(url);
        if (matcher.find()) {
            return requester.apply(matcher.group(1));
        } else {
            throw new MalformedUrlException();
        }
    }

    public static <T> T sendGithubRequest(String url, BiFunction<String, String, T> requester) throws MalformedUrlException {
        Matcher matcher = GITHUB_PATTERN.matcher(url);
        if (matcher.find()) {
            return requester.apply(matcher.group(1), matcher.group(2));
        } else {
            throw new MalformedUrlException();
        }
    }

    public static <T> T getFirstInSingleElementList(List<T> lst) {
        if (lst.size() != 1) {
            throw new IncorrectStackoverflowIdsParameter();
        }
        return lst.getFirst();
    }
}
