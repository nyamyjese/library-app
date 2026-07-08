package com.hei.school.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record GenreRevenueResponse(UUID genreId, String name, BigDecimal revenue) {}
