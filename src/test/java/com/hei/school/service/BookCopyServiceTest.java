package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.mapper.BookCopyMapper;
import com.hei.school.repository.BookCopyRepository;
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
  @Mock private BookCopyMapper bookCopyMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private final UUID copyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private BookCopy bookCopy;
  private BookCopyDTO dto;

  @BeforeEach
  void setUp() {
    Library library = new Library();
    library.setId(libraryId);

    Book book = new Book();
    book.setId(bookId);

    bookCopy = BookCopy.builder()
        .id(copyId)
        .format(BookCopyFormat.PHYSICAL)
        .isbn("978-1234567890")
        .sellingPrice(BigDecimal.valueOf(20.00))
        .status(BookCopyStatus.AVAILABLE)
        .book(book)
        .library(library)
        .build();

    dto = BookCopyDTO.builder()
        .id(copyId)
        .bookId(bookId)
        .libraryId(libraryId)
        .format(BookCopyFormat.PHYSICAL)
        .isbn("978-1234567890")
        .sellingPrice(BigDecimal.valueOf(20.00))
        .status(BookCopyStatus.AVAILABLE)
        .build();
  }

  @Test
  void getById_Success() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    BookCopyDTO result = bookCopyService.getById(copyId);
    assertThat(result.getId()).isEqualTo(copyId);
  }

  @Test
  void getById_NotFound() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> bookCopyService.getById(copyId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("not found");
  }

  @Test
  void getAll() {
    when(bookCopyRepository.findAll()).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    List<BookCopyDTO> result = bookCopyService.getAll();
    assertThat(result).hasSize(1);
  }

  @Test
  void getAvailable() {
    when(bookCopyRepository.findAllByStatus(BookCopyStatus.AVAILABLE)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    List<BookCopyDTO> result = bookCopyService.getAvailable();
    assertThat(result).hasSize(1);
  }

  @Test
  void getByBook() {
    when(bookCopyRepository.findAllByBook_Id(bookId)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    List<BookCopyDTO> result = bookCopyService.getByBook(bookId);
    assertThat(result).hasSize(1);
  }

  @Test
  void getAvailableByBook() {
    when(bookCopyRepository.findAllByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE))
        .thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    List<BookCopyDTO> result = bookCopyService.getAvailableByBook(bookId);
    assertThat(result).hasSize(1);
  }

  @Test
  void getByLibrary() {
    when(bookCopyRepository.findAllByLibrary_Id(libraryId)).thenReturn(List.of(bookCopy));
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    List<BookCopyDTO> result = bookCopyService.getByLibrary(libraryId);
    assertThat(result).hasSize(1);
  }

  @Test
  void updateStatus_Success() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);
    when(bookCopyMapper.toDTO(bookCopy)).thenReturn(dto);
    BookCopyDTO result = bookCopyService.updateStatus(copyId, BookCopyStatus.DAMAGED);
    assertThat(result.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
  }

  @Test
  void updateStatus_NotFound() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> bookCopyService.updateStatus(copyId, BookCopyStatus.DAMAGED))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("not found");
  }

  @Test
  void countAvailableByBook() {
    when(bookCopyRepository.countByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE))
        .thenReturn(5L);
    long count = bookCopyService.countAvailableByBook(bookId);
    assertThat(count).isEqualTo(5L);
  }
}
