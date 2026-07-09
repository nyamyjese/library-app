package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.request.UpdateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.BookCopyMapper;
import com.hei.school.repository.BookCopyRepository;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.LibraryRepository;
import com.hei.school.repository.StockMovementRepository;
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
class BookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private BookRepository bookRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private StockMovementRepository stockMovementRepository;
  @Mock private BookCopyMapper bookCopyMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private final UUID copyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private BookCopy bookCopy;
  private BookCopyResponse response;

  @BeforeEach
  void setUp() {
    var book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");
    var library = new Library();
    library.setId(libraryId);
    library.setName("Main Library");

    bookCopy = BookCopy.builder()
        .id(copyId)
        .format(BookCopyFormat.PHYSICAL)
        .isbn("978-1234567890")
        .sellingPrice(BigDecimal.valueOf(20.00))
        .status(BookCopyStatus.AVAILABLE)
        .book(book)
        .library(library)
        .build();

    response = new BookCopyResponse(copyId, bookId, "Test Book", libraryId, "Main Library",
        BookCopyFormat.PHYSICAL, "978-1234567890", BigDecimal.valueOf(20.00),
        BookCopyStatus.AVAILABLE);
  }

  @Test
  void create_Success() {
    var request = new CreateBookCopyRequest(bookId, libraryId, BookCopyFormat.PHYSICAL,
        "978-1234567890", BigDecimal.valueOf(20.00), BookCopyStatus.AVAILABLE);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(new Library()));
    when(bookCopyMapper.toEntity(any(), any(), any())).thenReturn(bookCopy);
    when(bookCopyRepository.save(bookCopy)).thenReturn(bookCopy);
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(response);

    var result = bookCopyService.create(request);

    assertThat(result.id()).isEqualTo(copyId);
  }

  @Test
  void create_BookNotFound() {
    var request = new CreateBookCopyRequest(bookId, libraryId, BookCopyFormat.PHYSICAL,
        "978-1234567890", BigDecimal.valueOf(20.00), BookCopyStatus.AVAILABLE);
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.create(request))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getById_Success() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(response);

    var result = bookCopyService.getById(copyId);

    assertThat(result.id()).isEqualTo(copyId);
  }

  @Test
  void getById_NotFound() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.getById(copyId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getAll() {
    when(bookCopyRepository.findAll()).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(response);

    var result = bookCopyService.getAll(null, null, null);

    assertThat(result).hasSize(1);
  }

  @Test
  void getAll_ByBook() {
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(response);

    var result = bookCopyService.getAll(bookId, null, null);

    assertThat(result).hasSize(1);
  }

  @Test
  void update_Success() {
    var request = new UpdateBookCopyRequest(BookCopyFormat.DIGITAL,
        BigDecimal.valueOf(25.00), BookCopyStatus.DAMAGED);
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(bookCopy)).thenReturn(bookCopy);
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(response);

    var result = bookCopyService.update(copyId, request);

    assertThat(result).isNotNull();
  }

  @Test
  void update_NotFound() {
    var request = new UpdateBookCopyRequest(BookCopyFormat.DIGITAL,
        BigDecimal.valueOf(25.00), BookCopyStatus.DAMAGED);
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.update(copyId, request))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void delete_Success() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    bookCopyService.delete(copyId);

    verify(bookCopyRepository).deleteById(copyId);
  }

  @Test
  void delete_NotFound() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.delete(copyId))
        .isInstanceOf(NotFoundException.class);
  }
}
