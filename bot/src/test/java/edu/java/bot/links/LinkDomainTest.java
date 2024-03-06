package edu.java.bot.links;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static edu.java.bot.links.LinkDomain.GITHUB;
import static edu.java.bot.links.LinkDomain.SOF;
import static edu.java.bot.links.LinkDomain.UNSUPPORTED;
import static edu.java.bot.links.LinkDomain.inferDomain;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkDomainTest {
    @Test
    @DisplayName("Infer domain")
    void inferDomain_whenURL_thenItsDomain() throws Exception {
        assertThat(inferDomain(new URI("https://stackoverflow.com").toURL())).isEqualTo(SOF);
        assertThat(inferDomain(new URI("https://stackoverflow.com/asdsadsad").toURL())).isEqualTo(SOF);
        assertThat(inferDomain(new URI("https://github.com").toURL())).isEqualTo(GITHUB);
        assertThat(inferDomain(new URI("https://github.com/asdsa/asdsad").toURL())).isEqualTo(GITHUB);
        assertThat(inferDomain(new URI("https://123.org").toURL())).isEqualTo(UNSUPPORTED);
    }
}
