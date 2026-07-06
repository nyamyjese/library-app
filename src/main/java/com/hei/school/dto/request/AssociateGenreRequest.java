package com.hei.school.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssociateGenreRequest(@NotNull UUID bookId, @NotNull UUID genreId) {}
