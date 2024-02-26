package edu.java.bot.links;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static edu.java.bot.links.Link.isLinkCorrect;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkTest {
    @Test
    @DisplayName("Incorrect links")
    void isLinkCorrect_whenMalformed_thenFalse() {
        assertThat(isLinkCorrect("wadhgjefhjewfew")).isFalse();
        assertThat(isLinkCorrect("123.com")).isFalse();
        assertThat(isLinkCorrect("www.123.com")).isFalse();
        assertThat(isLinkCorrect("google.com")).isFalse();
    }

    @Test
    @DisplayName("Unknown domain")
    void isLinkCorrect_whenUnknownDomain_thenFalse() {
        assertThat(isLinkCorrect("http://ya.ru")).isFalse();
        assertThat(isLinkCorrect("http://www.ya.ru")).isFalse();
    }

    @Test
    @DisplayName("Known domain")
    void isLinkCorrect_whenKnownDomain_thenTrue() {
        assertThat(isLinkCorrect("http://github.com")).isTrue();
        assertThat(isLinkCorrect("http://www.github.com")).isTrue();
        assertThat(isLinkCorrect("http://www.stackoverflow.com")).isTrue();
        assertThat(isLinkCorrect("http://stackoverflow.com")).isTrue();
    }
}
