package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.dto.response.GenreRevenueResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.Genre;
import com.hei.school.exception.ConflictException;
import com.hei.school.exception.NotFoundException;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.GenreRepository;
import com.hei.school.repository.SaleItemRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
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
class GenreServiceTest {

  @Mock private GenreRepository genreRepository;

  @Mock private BookRepository bookRepository;

  @Mock private SaleItemRepository saleItemRepository;

  @InjectMocks private GenreService genreService;

  private UUID genreId;
  private UUID bookId;
  private Genre genre;
  private Book book;

  @BeforeEach
  void setUp() {
    genreId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    genre = Genre.builder().id(genreId).name("Fiction").build();
    book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");
    book.setGenres(new ArrayList<>());
  }

  // ----- getRevenue() -----
  @Test
  void getRevenue_shouldReturnRevenue_whenGenreExists() {
    BigDecimal expectedRevenue = new BigDecimal("250.00");
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(saleItemRepository.sumRevenueByGenreId(genreId)).thenReturn(expectedRevenue);

    GenreRevenueResponse response = genreService.getRevenue(genreId);

    assertThat(response.genreId()).isEqualTo(genreId);
    assertThat(response.name()).isEqualTo("Fiction");
    assertThat(response.revenue()).isEqualByComparingTo(expectedRevenue);
    verify(genreRepository).findById(genreId);
    verify(saleItemRepository).sumRevenueByGenreId(genreId);
  }

  @Test
  void getRevenue_shouldReturnZeroRevenue_whenNoSales() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(saleItemRepository.sumRevenueByGenreId(genreId)).thenReturn(null);

    GenreRevenueResponse response = genreService.getRevenue(genreId);

    assertThat(response.revenue()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void getRevenue_shouldThrowNotFoundException_whenGenreNotFound() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.getRevenue(genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Genre with id " + genreId + " not found");
  }

  // ----- createGenre() -----
  @Test
  void createGenre_shouldSaveAndReturnGenreResponse() {
    GenreCreateRequest request = new GenreCreateRequest("Science Fiction");
    Genre savedGenre = Genre.builder().id(UUID.randomUUID()).name("Science Fiction").build();
    when(genreRepository.save(any(Genre.class))).thenReturn(savedGenre);

    GenreResponse response = genreService.createGenre(request);

    assertThat(response.id()).isNotNull();
    assertThat(response.name()).isEqualTo("Science Fiction");
    verify(genreRepository).save(any(Genre.class));
  }

  // ----- updateGenre() -----
  @Test
  void updateGenre_shouldUpdateAndReturnGenreResponse_whenExists() {
    UUID id = genreId;
    GenreUpdateRequest request = new GenreUpdateRequest("Mystery");
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
    when(genreRepository.save(any(Genre.class))).thenReturn(genre);

    GenreResponse response = genreService.updateGenre(id, request);

    assertThat(response.name()).isEqualTo("Mystery");
    verify(genreRepository).findById(id);
    verify(genreRepository).save(genre);
  }

  @Test
  void updateGenre_shouldThrowNotFoundException_whenGenreNotFound() {
    UUID id = UUID.randomUUID();
    GenreUpdateRequest request = new GenreUpdateRequest("Mystery");
    when(genreRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.updateGenre(id, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Genre not found: " + id);
    verify(genreRepository, never()).save(any());
  }

  // ----- deleteGenre() -----
  @Test
  void deleteGenre_shouldDelete_whenGenreExists() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.deleteGenre(genreId);

    verify(genreRepository).delete(genre);
  }

  @Test
  void deleteGenre_shouldThrowNotFoundException_whenGenreNotFound() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.deleteGenre(genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Genre not found: " + genreId);
    verify(genreRepository, never()).delete(any());
  }

  // ----- getGenreById() -----
  @Test
  void getGenreById_shouldReturnGenreResponse_whenExists() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    GenreResponse response = genreService.getGenreById(genreId);

    assertThat(response.id()).isEqualTo(genreId);
    assertThat(response.name()).isEqualTo("Fiction");
    verify(genreRepository).findById(genreId);
  }

  @Test
  void getGenreById_shouldThrowNotFoundException_whenNotFound() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.getGenreById(genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Genre not found: " + genreId);
  }

  // ----- getAllGenres() -----
  @Test
  void getAllGenres_shouldReturnListOfGenreResponses() {
    Genre genre2 = Genre.builder().id(UUID.randomUUID()).name("Fantasy").build();
    when(genreRepository.findAll()).thenReturn(List.of(genre, genre2));

    List<GenreResponse> responses = genreService.getAllGenres();

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).name()).isEqualTo("Fiction");
    assertThat(responses.get(1).name()).isEqualTo("Fantasy");
    verify(genreRepository).findAll();
  }

  // ----- searchGenresByName() -----
  @Test
  void searchGenresByName_shouldReturnFilteredList() {
    String name = "Fic";
    when(genreRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of(genre));

    List<GenreResponse> responses = genreService.searchGenresByName(name);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).name()).isEqualTo("Fiction");
    verify(genreRepository).findByNameContainingIgnoreCase(name);
  }

  // ----- addGenreToBook() -----
  @Test
  void addGenreToBook_shouldAddGenre_whenNotAlreadyAssociated() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.addGenreToBook(bookId, genreId);

    assertThat(book.getGenres()).contains(genre);
    verify(bookRepository).save(book);
  }

  @Test
  void addGenreToBook_shouldThrowConflictException_whenAlreadyAssociated() {
    book.getGenres().add(genre);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    assertThatThrownBy(() -> genreService.addGenreToBook(bookId, genreId))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Association already exists");
    verify(bookRepository, never()).save(book);
  }

  @Test
  void addGenreToBook_shouldThrowNotFoundException_whenBookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.addGenreToBook(bookId, genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found: " + bookId);
  }

  @Test
  void addGenreToBook_shouldThrowNotFoundException_whenGenreNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.addGenreToBook(bookId, genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Genre not found: " + genreId);
  }

  // ----- removeGenreFromBook() -----
  @Test
  void removeGenreFromBook_shouldRemoveGenre_whenAssociated() {
    book.getGenres().add(genre);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.removeGenreFromBook(bookId, genreId);

    assertThat(book.getGenres()).doesNotContain(genre);
    verify(bookRepository).save(book);
  }

  @Test
  void removeGenreFromBook_shouldThrowNotFoundException_whenNotAssociated() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    assertThatThrownBy(() -> genreService.removeGenreFromBook(bookId, genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Association not found");
    verify(bookRepository, never()).save(any());
  }

  @Test
  void removeGenreFromBook_shouldThrowNotFoundException_whenBookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.removeGenreFromBook(bookId, genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found: " + bookId);
  }

  // ----- getGenresByBook() -----
  @Test
  void getGenresByBook_shouldReturnListOfGenres() {
    book.getGenres().add(genre);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    List<GenreResponse> responses = genreService.getGenresByBook(bookId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).id()).isEqualTo(genreId);
    verify(bookRepository).findById(bookId);
  }

  @Test
  void getGenresByBook_shouldReturnEmptyList_whenBookHasNoGenres() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    List<GenreResponse> responses = genreService.getGenresByBook(bookId);

    assertThat(responses).isEmpty();
  }

  @Test
  void getGenresByBook_shouldThrowNotFoundException_whenBookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.getGenresByBook(bookId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found: " + bookId);
  }

  // ----- getBooksByGenre() -----
  @Test
  void getBooksByGenre_shouldReturnListOfBookIds() {
    Book book2 = new Book();
    book2.setId(UUID.randomUUID());
    book2.setGenres(List.of(genre));
    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookRepository.findAll()).thenReturn(List.of(book, book2));

    List<UUID> bookIds = genreService.getBooksByGenre(genreId);

    assertThat(bookIds).containsExactly(book.getId(), book2.getId());
    verify(genreRepository).existsById(genreId);
    verify(bookRepository).findAll();
  }

  @Test
  void getBooksByGenre_shouldThrowNotFoundException_whenGenreNotFound() {
    when(genreRepository.existsById(genreId)).thenReturn(false);

    assertThatThrownBy(() -> genreService.getBooksByGenre(genreId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Genre not found: " + genreId);
  }
}
