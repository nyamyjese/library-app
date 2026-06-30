package com.hei.school.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    Instant createdAt,
    Instant updatedAt) {}
