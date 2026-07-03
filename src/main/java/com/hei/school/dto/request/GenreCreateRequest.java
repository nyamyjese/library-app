package com.hei.school.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenreCreateRequest(@NotBlank String name) {}
