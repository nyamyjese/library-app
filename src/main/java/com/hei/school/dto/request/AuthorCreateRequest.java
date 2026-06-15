package com.hei.school.dto.request;

import java.time.Instant;

import com.hei.school.entity.enums.Sexe;

public record AuthorCreateRequest(String firstName, String lastName, Sexe sexe, Instant createdDate) {
}
