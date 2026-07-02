package com.hei.school.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthorUpdateRequest(
    @NotBlank String firstName,
    @NotBlank String lastName) {}