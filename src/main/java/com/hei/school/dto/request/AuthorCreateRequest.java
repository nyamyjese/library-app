package com.hei.school.dto.request;

import com.hei.school.entity.enums.Sexe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorCreateRequest(
    @NotBlank String firstName, @NotBlank String lastName, @NotNull Sexe sexe) {}
