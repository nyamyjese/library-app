package com.hei.school.dto.request;

import java.util.UUID;

public record AuthorUpdateRequest(UUID id, String firstName, String lastName) {}
