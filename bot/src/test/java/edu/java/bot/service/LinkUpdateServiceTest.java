package edu.java.bot.service;

import edu.java.bot.PrimaveraBot;
import edu.java.model.dto.LinkUpdate;
import edu.java.model.exceptions.MalformedUrlException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class LinkUpdateServiceTest {
    @Mock
    private PrimaveraBot primaveraBot;
    LinkUpdateService service;

    @BeforeEach
    void setUp() {
        service = new LinkUpdateService(primaveraBot);
    }

    @Test
    public void processUpdate_whenIncorrectLink_thenMalformedUrlException() {
        assertThatThrownBy(() -> service.processUpdate(
            new LinkUpdate(1L, new URI("https://google.com"), "sda", List.of())))
            .isInstanceOf(MalformedUrlException.class)
            .hasMessage("URL provided isn't supported");
    }

    @Test
    public void processUpdate_whenCorrectLink_thenOk() throws Exception {
        service.processUpdate(
            new LinkUpdate(1L, new URI("https://github.com"), "sda", List.of(1L, 2L, 3L)));
        verify(primaveraBot).message(1L, "The following URL: https://github.com with description sda has been updated");
        verify(primaveraBot).message(2L, "The following URL: https://github.com with description sda has been updated");
        verify(primaveraBot).message(3L, "The following URL: https://github.com with description sda has been updated");
        verifyNoMoreInteractions(primaveraBot);
    }
}
