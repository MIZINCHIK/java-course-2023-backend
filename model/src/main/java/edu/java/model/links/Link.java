package edu.java.model.links;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.EqualsAndHashCode;
import static edu.java.model.links.LinkDomain.UNSUPPORTED;
import static edu.java.model.links.LinkDomain.inferDomain;

@EqualsAndHashCode
public class Link {
    private final URL url;
    private final LinkDomain domain;

    public Link(String url) {
        try {
            this.url = convertStringToUrl(url);
            domain = inferDomain(this.url);
            if (domain == UNSUPPORTED) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Link(URL url) {
        domain = inferDomain(url);
        if (domain == UNSUPPORTED) {
            throw new IllegalStateException();
        }
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public LinkDomain getDomain() {
        return domain;
    }

    public static boolean isLinkCorrect(String link) {
        try {
            URL url = new URI(link).toURL();
            return inferDomain(url) != UNSUPPORTED;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static URL convertStringToUrl(String link) throws URISyntaxException, MalformedURLException {
        return new URI(link).toURL();
    }
}
