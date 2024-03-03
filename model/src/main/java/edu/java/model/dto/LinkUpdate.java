package edu.java.model.dto;

import java.net.URI;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record LinkUpdate(@NotNull Long id, @NotNull URI url, @NotNull String description,
                         @NotNull List<Long> tgChatIds) {
}
