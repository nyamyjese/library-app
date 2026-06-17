package com.hei.school.dto.request;

import java.util.UUID;

public record AssociateAuthorRequest(UUID bookId, UUID authorId) {}
