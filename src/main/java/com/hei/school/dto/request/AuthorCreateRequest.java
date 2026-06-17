package com.hei.school.dto.request;

import com.hei.school.entity.enums.Sexe;
import java.time.Instant;

public record AuthorCreateRequest(
    String firstName, String lastName, Sexe sexe, Instant createdDate) {}
