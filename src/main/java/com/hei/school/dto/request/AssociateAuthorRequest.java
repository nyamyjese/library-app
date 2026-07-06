package com.hei.school.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssociateAuthorRequest(@NotNull UUID bookId, @NotNull UUID authorId) {}
