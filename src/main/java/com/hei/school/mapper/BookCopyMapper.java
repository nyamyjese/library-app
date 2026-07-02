package com.hei.school.mapper;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.BookCopy;
import org.springframework.stereotype.Component;

@Component
public class BookCopyMapper {

    public BookCopyDTO toDTO(BookCopy c) {
        return BookCopyDTO.builder()
                .id(c.getId())
                .bookId(c.getBook() != null ? c.getBook().getId() : null)
                .libraryId(c.getLibrary() != null ? c.getLibrary().getLibraryId() : null)
                .format(c.getFormat())
                .isbn(c.getIsbn())
                .sellingPrice(c.getSellingPrice())
                .status(c.getStatus())
                .build();
    }
}