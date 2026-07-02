package com.hei.school.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenreUpdateRequest(@NotBlank String name) {}