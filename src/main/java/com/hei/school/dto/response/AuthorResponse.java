package com.hei.school.dto.response;

import java.util.UUID;

public record AuthorResponse(UUID id_author, String firstName, String lastName) {
}
