package com.hei.school.dto.request;

import java.util.UUID;

public record AssociateGenreRequest(UUID bookId, UUID genreId) {

}
