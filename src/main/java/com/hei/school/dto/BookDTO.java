package com.hei.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private UUID id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private BigDecimal price;
    private Long libraryId;
}
