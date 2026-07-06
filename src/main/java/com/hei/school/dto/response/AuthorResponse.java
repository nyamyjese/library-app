package com.hei.school.dto.response;

import com.hei.school.entity.enums.Sexe;
import java.util.UUID;

public record AuthorResponse(UUID id, String firstName, String lastName, Sexe sexe) {}
