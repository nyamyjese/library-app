package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.request.LowStockResponse;
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

  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();

  private Book book;
  private Library library;
  private BookCopy bookCopy;
  private BookCopyResponse bookCopyResponse;

  @BeforeEach
  void setUp() {
    book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");

    library = new Library();
    library.setId(libraryId);
    library.setName("Test Library");

    bookCopy = new BookCopy();
    bookCopy.setId(bookCopyId);
    bookCopy.setBook(book);
    bookCopy.setLibrary(library);
    bookCopy.setFormat(BookCopyFormat.PHYSICAL);
    bookCopy.setIsbn("9781234567890");
    bookCopy.setSellingPrice(new BigDecimal("29.99"));
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);

    bookCopyResponse =
        new BookCopyResponse(
            bookCopyId,
            bookId,
            "Test Book",
            libraryId,
            "Test Library",
            BookCopyFormat.PHYSICAL,
            "9781234567890",
            new BigDecimal("29.99"),
            BookCopyStatus.AVAILABLE);
  }

  @Test
  void create_Success() {
    CreateBookCopyRequest request =
        new CreateBookCopyRequest(
            bookId,
            libraryId,
            BookCopyFormat.PHYSICAL,
            "9781234567890",
            new BigDecimal("29.99"),
            BookCopyStatus.AVAILABLE);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(bookCopyMapper.toEntity(request, book, library)).thenReturn(bookCopy);
    when(bookCopyRepository.save(bookCopy)).thenReturn(bookCopy);
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    BookCopyResponse result = bookCopyService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(bookCopyId);
    assertThat(result.bookId()).isEqualTo(bookId);
    assertThat(result.libraryId()).isEqualTo(libraryId);
    verify(bookRepository).findById(bookId);
    verify(libraryRepository).findById(libraryId);
    verify(bookCopyMapper).toEntity(request, book, library);
    verify(bookCopyRepository).save(bookCopy);
    verify(bookCopyMapper).toResponse(bookCopy);
  }

  @Test
  void create_BookNotFound() {
    CreateBookCopyRequest request =
        new CreateBookCopyRequest(
            bookId,
            libraryId,
            BookCopyFormat.PHYSICAL,
            "9781234567890",
            new BigDecimal("29.99"),
            BookCopyStatus.AVAILABLE);

    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.create(request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book with id " + bookId);

    verify(bookRepository).findById(bookId);
    verify(libraryRepository, never()).findById(any());
    verify(bookCopyRepository, never()).save(any());
  }

  @Test
  void create_LibraryNotFound() {
    CreateBookCopyRequest request =
        new CreateBookCopyRequest(
            bookId,
            libraryId,
            BookCopyFormat.PHYSICAL,
            "9781234567890",
            new BigDecimal("29.99"),
            BookCopyStatus.AVAILABLE);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.create(request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Library with id " + libraryId);

    verify(bookRepository).findById(bookId);
    verify(libraryRepository).findById(libraryId);
    verify(bookCopyRepository, never()).save(any());
  }

  @Test
  void getAll_WithoutFilters() {
    when(bookCopyRepository.findAll()).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    List<BookCopyResponse> result = bookCopyService.getAll(null, null, null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).id()).isEqualTo(bookCopyId);
    verify(bookCopyRepository).findAll();
    verify(bookCopyMapper).toResponse(bookCopy);
  }

  @Test
  void getAll_FilterByBookId() {
    UUID filterBookId = UUID.randomUUID();
    when(bookCopyRepository.findByBookId(filterBookId)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    List<BookCopyResponse> result = bookCopyService.getAll(filterBookId, null, null);

    assertThat(result).hasSize(1);
    verify(bookCopyRepository).findByBookId(filterBookId);
    verify(bookCopyRepository, never()).findAll();
  }

  @Test
  void getAll_FilterByLibraryId() {
    UUID filterLibraryId = UUID.randomUUID();
    when(bookCopyRepository.findByLibraryId(filterLibraryId)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    List<BookCopyResponse> result = bookCopyService.getAll(null, filterLibraryId, null);

    assertThat(result).hasSize(1);
    verify(bookCopyRepository).findByLibraryId(filterLibraryId);
    verify(bookCopyRepository, never()).findAll();
  }

  @Test
  void getAll_FilterByStatus() {
    when(bookCopyRepository.findByStatus(BookCopyStatus.AVAILABLE)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    List<BookCopyResponse> result = bookCopyService.getAll(null, null, BookCopyStatus.AVAILABLE);

    assertThat(result).hasSize(1);
    verify(bookCopyRepository).findByStatus(BookCopyStatus.AVAILABLE);
    verify(bookCopyRepository, never()).findAll();
  }

  @Test
  void getById_Success() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    BookCopyResponse result = bookCopyService.getById(bookCopyId);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(bookCopyId);
    verify(bookCopyRepository).findById(bookCopyId);
  }

  @Test
  void getById_NotFound() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.getById(bookCopyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id " + bookCopyId);

    verify(bookCopyRepository).findById(bookCopyId);
  }

  @Test
  void update_Success() {
    UpdateBookCopyRequest request =
        new UpdateBookCopyRequest(
            BookCopyFormat.DIGITAL, new BigDecimal("19.99"), BookCopyStatus.DAMAGED);

    BookCopy updatedBookCopy = new BookCopy();
    updatedBookCopy.setId(bookCopyId);
    updatedBookCopy.setBook(book);
    updatedBookCopy.setLibrary(library);
    updatedBookCopy.setFormat(BookCopyFormat.DIGITAL);
    updatedBookCopy.setIsbn("9781234567890");
    updatedBookCopy.setSellingPrice(new BigDecimal("19.99"));
    updatedBookCopy.setStatus(BookCopyStatus.DAMAGED);

    BookCopyResponse updatedResponse =
        new BookCopyResponse(
            bookCopyId,
            bookId,
            "Test Book",
            libraryId,
            "Test Library",
            BookCopyFormat.DIGITAL,
            "9781234567890",
            new BigDecimal("19.99"),
            BookCopyStatus.DAMAGED);

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(bookCopy)).thenReturn(updatedBookCopy);
    when(bookCopyMapper.toResponse(updatedBookCopy)).thenReturn(updatedResponse);

    BookCopyResponse result = bookCopyService.update(bookCopyId, request);

    assertThat(result).isNotNull();
    assertThat(result.format()).isEqualTo(BookCopyFormat.DIGITAL);
    assertThat(result.sellingPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
    assertThat(result.status()).isEqualTo(BookCopyStatus.DAMAGED);
    verify(bookCopyRepository).findById(bookCopyId);
    verify(bookCopyRepository).save(bookCopy);
  }

  @Test
  void update_NotFound() {
    UpdateBookCopyRequest request =
        new UpdateBookCopyRequest(
            BookCopyFormat.DIGITAL, new BigDecimal("19.99"), BookCopyStatus.DAMAGED);

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.update(bookCopyId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id " + bookCopyId);

    verify(bookCopyRepository).findById(bookCopyId);
    verify(bookCopyRepository, never()).save(any());
  }

  @Test
  void delete_Success() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));

    bookCopyService.delete(bookCopyId);

    verify(bookCopyRepository).findById(bookCopyId);
    verify(bookCopyRepository).deleteById(bookCopyId);
  }

  @Test
  void delete_NotFound() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.delete(bookCopyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id " + bookCopyId);

    verify(bookCopyRepository).findById(bookCopyId);
    verify(bookCopyRepository, never()).deleteById(any());
  }

  @Test
  void getLowStock_WithThreshold() {
    UUID lowStockId = UUID.randomUUID();
    int threshold = 5;

    when(stockMovementRepository.findBookCopyIdsWithLowStock(threshold))
        .thenReturn(List.of(lowStockId));

    BookCopy lowStockBookCopy = new BookCopy();
    lowStockBookCopy.setId(lowStockId);
    lowStockBookCopy.setBook(book);
    lowStockBookCopy.setLibrary(library);
    lowStockBookCopy.setFormat(BookCopyFormat.PHYSICAL);
    lowStockBookCopy.setIsbn("9780987654321");
    lowStockBookCopy.setSellingPrice(new BigDecimal("9.99"));
    lowStockBookCopy.setStatus(BookCopyStatus.AVAILABLE);

    when(bookCopyRepository.findById(lowStockId)).thenReturn(Optional.of(lowStockBookCopy));
    when(stockMovementRepository.getTotalStockByBookCopyId(lowStockId)).thenReturn(3);

    List<LowStockResponse> result = bookCopyService.getLowStock(threshold);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).bookCopyId()).isEqualTo(lowStockId);
    assertThat(result.get(0).bookId()).isEqualTo(bookId);
    assertThat(result.get(0).bookTitle()).isEqualTo("Test Book");
    assertThat(result.get(0).currentStock()).isEqualTo(3);
    verify(stockMovementRepository).findBookCopyIdsWithLowStock(threshold);
    verify(bookCopyRepository).findById(lowStockId);
    verify(stockMovementRepository).getTotalStockByBookCopyId(lowStockId);
  }

  @Test
  void getLowStock_DefaultThreshold() {
    UUID lowStockId = UUID.randomUUID();

    when(stockMovementRepository.findBookCopyIdsWithLowStock(3)).thenReturn(List.of(lowStockId));

    BookCopy lowStockBookCopy = new BookCopy();
    lowStockBookCopy.setId(lowStockId);
    lowStockBookCopy.setBook(book);
    lowStockBookCopy.setLibrary(library);
    lowStockBookCopy.setFormat(BookCopyFormat.PHYSICAL);
    lowStockBookCopy.setIsbn("9780987654321");
    lowStockBookCopy.setSellingPrice(new BigDecimal("9.99"));
    lowStockBookCopy.setStatus(BookCopyStatus.AVAILABLE);

    when(bookCopyRepository.findById(lowStockId)).thenReturn(Optional.of(lowStockBookCopy));
    when(stockMovementRepository.getTotalStockByBookCopyId(lowStockId)).thenReturn(1);

    List<LowStockResponse> result = bookCopyService.getLowStock(null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).currentStock()).isEqualTo(1);
    verify(stockMovementRepository).findBookCopyIdsWithLowStock(3);
  }

  @Test
  void getLowStock_BookCopyNotFound() {
    UUID missingId = UUID.randomUUID();

    when(stockMovementRepository.findBookCopyIdsWithLowStock(3)).thenReturn(List.of(missingId));
    when(bookCopyRepository.findById(missingId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.getLowStock(null))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id " + missingId);
  }
}
