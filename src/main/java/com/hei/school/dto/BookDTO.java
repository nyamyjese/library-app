package com.hei.school.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookDTO {
    private Long bookId;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private BigDecimal price;
    private Long libraryId;
}
