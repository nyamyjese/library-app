package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.Genre;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.GenreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

  @Mock private GenreRepository genreRepository;
  @Mock private BookRepository bookRepository;

  @InjectMocks private GenreService genreService;

  private final UUID genreId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();

  private Genre createSampleGenre() {
    return Genre.builder().id(genreId).name("Fantasy").build();
  }

  private Book createSampleBook() {
    Book book = new Book();
    book.setId(bookId);
    book.setTitle("Sample Book");
    book.setGenres(new ArrayList<>());
    return book;
  }

  @Test
  void testCreateGenre_Success() {
    GenreCreateRequest request = new GenreCreateRequest("Fantasy");
    Genre savedGenre = createSampleGenre();

    when(genreRepository.save(any(Genre.class))).thenReturn(savedGenre);

    GenreResponse response = genreService.createGenre(request);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(genreId);
    verify(genreRepository).save(any(Genre.class));
  }

  @Test
  void testUpdateGenre_Success() {
    GenreUpdateRequest request = new GenreUpdateRequest("Sci-Fi");
    Genre existingGenre = createSampleGenre();
    Genre updatedGenre = Genre.builder().id(genreId).name("Sci-Fi").build();

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(existingGenre));
    when(genreRepository.save(any(Genre.class))).thenReturn(updatedGenre);

    GenreResponse response = genreService.updateGenre(genreId, request);

    assertThat(response).isNotNull();
    assertThat(response.name()).isEqualTo("Sci-Fi");
  }

  @Test
  void testUpdateGenre_NotFound() {
    GenreUpdateRequest request = new GenreUpdateRequest("Sci-Fi");
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.updateGenre(genreId, request))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
  }

  @Test
  void testDeleteGenre_Success() {
    Genre existingGenre = createSampleGenre();
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(existingGenre));

    genreService.deleteGenre(genreId);

    verify(genreRepository).delete(existingGenre);
  }

  @Test
  void testGetGenreById_Success() {
    Genre genre = createSampleGenre();
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    GenreResponse response = genreService.getGenreById(genreId);

    assertThat(response).isNotNull();
    assertThat(response.name()).isEqualTo("Fantasy");
  }

  @Test
  void testGetAllGenres() {
    Genre genre = createSampleGenre();
    when(genreRepository.findAll()).thenReturn(List.of(genre));

    List<GenreResponse> responses = genreService.getAllGenres();

    assertThat(responses).hasSize(1);
  }

  @Test
  void testSearchGenresByName() {
    Genre genre = createSampleGenre();
    when(genreRepository.findByNameContainingIgnoreCase("Fan")).thenReturn(List.of(genre));

    List<GenreResponse> responses = genreService.searchGenresByName("Fan");

    assertThat(responses).hasSize(1);
  }

  @Test
  void testAddGenreToBook_Success() {
    Book book = createSampleBook();
    Genre genre = createSampleGenre();

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.addGenreToBook(bookId, genreId);

    assertThat(book.getGenres()).contains(genre);
    verify(bookRepository).save(book);
  }

  @Test
  void testRemoveGenreFromBook_Success() {
    Book book = createSampleBook();
    Genre genre = createSampleGenre();
    book.getGenres().add(genre);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.removeGenreFromBook(bookId, genreId);

    assertThat(book.getGenres()).doesNotContain(genre);
    verify(bookRepository).save(book);
  }

  @Test
  void testGetGenresByBook() {
    Book book = createSampleBook();
    Genre genre = createSampleGenre();
    book.getGenres().add(genre);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    List<GenreResponse> responses = genreService.getGenresByBook(bookId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).name()).isEqualTo("Fantasy");
  }

  @Test
  void testGetBooksByGenre_Success() {
    Book book = createSampleBook();
    Genre genre = createSampleGenre();
    book.getGenres().add(genre);

    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<UUID> bookIds = genreService.getBooksByGenre(genreId);

    assertThat(bookIds).containsExactly(bookId);
  }
}
