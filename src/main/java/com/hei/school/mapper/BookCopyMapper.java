package com.hei.school.mapper;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import org.springframework.stereotype.Component;

@Component
public class BookCopyMapper {

  public BookCopyDTO toDTO(BookCopy c) {
    return BookCopyDTO.builder()
        .id(c.getId())
        .bookId(c.getBook() != null ? c.getBook().getId() : null)
        .libraryId(c.getLibrary() != null ? c.getLibrary().getId() : null)
        .format(c.getFormat())
        .isbn(c.getIsbn())
        .sellingPrice(c.getSellingPrice())
        .status(c.getStatus())
        .build();
  }

  public BookCopy toEntity(CreateBookCopyRequest request, Book book, Library library) {
    BookCopy bookCopy = new BookCopy();
    bookCopy.setBook(book);
    bookCopy.setLibrary(library);
    bookCopy.setFormat(request.format());
    bookCopy.setIsbn(request.isbn());
    bookCopy.setSellingPrice(request.sellingPrice());
    bookCopy.setStatus(request.status());
    return bookCopy;
  }

  public BookCopyResponse toResponse(BookCopy bookCopy) {
    return new BookCopyResponse(
        bookCopy.getId(),
        bookCopy.getBook().getId(),
        bookCopy.getBook().getTitle(),
        bookCopy.getLibrary().getId(),
        bookCopy.getLibrary().getName(),
        bookCopy.getFormat(),
        bookCopy.getIsbn(),
        bookCopy.getSellingPrice(),
        bookCopy.getStatus());
  }
}
