package edu.java.model.links;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static edu.java.model.links.LinkDomain.GITHUB;
import static edu.java.model.links.LinkDomain.STACKOVERFLOW;
import static edu.java.model.links.LinkDomain.UNSUPPORTED;
import static edu.java.model.links.LinkDomain.inferDomain;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkDomainTest {
    @Test
    @DisplayName("Infer domain")
    void inferDomain_whenURL_thenItsDomain() throws Exception {
        assertThat(inferDomain(new URI("https://stackoverflow.com").toURL())).isEqualTo(STACKOVERFLOW);
        assertThat(inferDomain(new URI("https://stackoverflow.com/asdsadsad").toURL())).isEqualTo(STACKOVERFLOW);
        assertThat(inferDomain(new URI("https://github.com").toURL())).isEqualTo(GITHUB);
        assertThat(inferDomain(new URI("https://github.com/asdsa/asdsad").toURL())).isEqualTo(GITHUB);
        assertThat(inferDomain(new URI("https://123.org").toURL())).isEqualTo(UNSUPPORTED);
    }
}
