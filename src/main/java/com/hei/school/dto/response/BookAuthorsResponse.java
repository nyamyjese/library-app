package com.hei.school.dto.response;

import java.util.List;
import java.util.UUID;

public record BookAuthorsResponse(UUID id, UUID bookId, List<AuthorResponse> authors) {}
