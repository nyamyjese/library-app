package com.hei.school.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hei.school.dto.BookDTO;
import com.hei.school.entity.Book;
import com.hei.school.entity.Library;
import com.hei.school.exception.NotFoundException;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.LibraryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
  @Mock private BookRepository bookRepository;

  @Mock private LibraryRepository libraryRepository;

  @InjectMocks private BookService bookService;

  private Book book;
  private Library library;
  private UUID bookId;
  private UUID libraryId;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    bookId = UUID.randomUUID();

    library = new Library();
    library.setId(libraryId);
    library.setName("Book");

    book = new Book();
    book.setId(bookId);
    book.setLibrary(library);
    book.setTitle("Le Petit Prince");
    book.setIsbn("978-2-07-040850-4");
    book.setPublicationYear(1943);
    book.setPrice(new BigDecimal("15.00"));
  }

  @Test
  void getAllBooks() {
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<BookDTO> result = bookService.getAll();

    assertEquals(1, result.size());
    assertEquals("Le Petit Prince", result.get(0).getTitle());
    verify(bookRepository, times(1)).findAll();
  }

  @Test
  void getById_ok() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    BookDTO result = bookService.getById(bookId);

    assertNotNull(result);
    assertEquals("Le Petit Prince", result.getTitle());
  }

  @Test
  void getById_notFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookService.getById(bookId));
  }

  @Test
  void delete_shouldDeleteBook_whenBookExists() {
    bookService.delete(bookId);
    verify(bookRepository, times(1)).deleteById(bookId);
  }
}
