package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.client.BookClient;
import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.entity.BookGenre;
import com.hei.school.entity.Genre;
import com.hei.school.repository.BookGenreRepository;
import com.hei.school.repository.GenreRepository;
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

  @Mock private BookGenreRepository bookGenreRepository;

  @Mock private BookClient bookClient;

  @InjectMocks private GenreService genreService;

  private final UUID genreId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();

  private Genre createSampleGenre() {
    return Genre.builder().id(genreId).name("Fantasy").build();
  }

  @Test
  void testCreateGenre_Success() {
    GenreCreateRequest request = new GenreCreateRequest("Science Fiction");
    Genre savedGenre = Genre.builder().id(UUID.randomUUID()).name("Science Fiction").build();
    when(genreRepository.save(any(Genre.class))).thenReturn(savedGenre);

    GenreResponse response = genreService.createGenre(request);

    assertThat(response.id()).isEqualTo(savedGenre.getId());
    assertThat(response.name()).isEqualTo("Science Fiction");
    verify(genreRepository).save(any(Genre.class));
  }

  @Test
  void testUpdateGenre_Success() {
    Genre existingGenre = createSampleGenre();
    GenreUpdateRequest request = new GenreUpdateRequest(genreId, "Fantasy Updated");
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(existingGenre));
    when(genreRepository.save(any(Genre.class))).thenReturn(existingGenre);

    GenreResponse response = genreService.updateGenre(request);

    assertThat(response.name()).isEqualTo("Fantasy Updated");
    verify(genreRepository).findById(genreId);
    verify(genreRepository).save(existingGenre);
  }

  @Test
  void testUpdateGenre_NotFound() {
    GenreUpdateRequest request = new GenreUpdateRequest(genreId, "Fantasy");
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.updateGenre(request))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
              assertThat(rse.getReason()).isEqualTo("Genre not found");
            });
    verify(genreRepository, never()).save(any());
  }

  @Test
  void testDeleteGenre_Success() {
    List<BookGenre> associations = List.of(BookGenre.builder().build());
    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookGenreRepository.findByGenreId(genreId)).thenReturn(associations);

    genreService.deleteGenre(genreId);

    verify(bookGenreRepository).deleteAll(associations);
    verify(genreRepository).deleteById(genreId);
  }

  @Test
  void testDeleteGenre_NotFound() {
    when(genreRepository.existsById(genreId)).thenReturn(false);

    assertThatThrownBy(() -> genreService.deleteGenre(genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookGenreRepository, never()).deleteAll(any());
    verify(genreRepository, never()).deleteById(any());
  }

  @Test
  void testGetGenreById_Success() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(createSampleGenre()));

    GenreResponse response = genreService.getGenreById(genreId);

    assertThat(response.id()).isEqualTo(genreId);
    assertThat(response.name()).isEqualTo("Fantasy");
    verify(genreRepository).findById(genreId);
  }

  @Test
  void testGetGenreById_NotFound() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.getGenreById(genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
  }

  @Test
  void testGetAllGenres() {
    when(genreRepository.findAll()).thenReturn(List.of(createSampleGenre()));

    List<GenreResponse> responses = genreService.getAllGenres();

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).name()).isEqualTo("Fantasy");
    verify(genreRepository).findAll();
  }

  @Test
  void testSearchGenresByName() {
    String name = "Fantasy";
    when(genreRepository.findByNameContainingIgnoreCase(name))
        .thenReturn(List.of(createSampleGenre()));

    List<GenreResponse> responses = genreService.searchGenresByName(name);

    assertThat(responses).hasSize(1);
    verify(genreRepository).findByNameContainingIgnoreCase(name);
  }

  @Test
  void testAddGenreToBook_Success() {
    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookClient.bookExists(bookId)).thenReturn(true);
    when(bookGenreRepository.existsByBookIdAndGenreId(bookId, genreId)).thenReturn(false);
    when(genreRepository.getReferenceById(genreId)).thenReturn(createSampleGenre());

    genreService.addGenreToBook(bookId, genreId);

    verify(bookGenreRepository).save(any(BookGenre.class));
  }

  @Test
  void testAddGenreToBook_GenreNotFound() {
    when(genreRepository.existsById(genreId)).thenReturn(false);

    assertThatThrownBy(() -> genreService.addGenreToBook(bookId, genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookGenreRepository, never()).save(any());
  }

  @Test
  void testAddGenreToBook_BookNotFound() {
    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookClient.bookExists(bookId)).thenReturn(false);

    assertThatThrownBy(() -> genreService.addGenreToBook(bookId, genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
              assertThat(rse.getReason()).isEqualTo("Book not found");
            });
    verify(bookGenreRepository, never()).save(any());
  }

  @Test
  void testAddGenreToBook_AlreadyExists() {
    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookClient.bookExists(bookId)).thenReturn(true);
    when(bookGenreRepository.existsByBookIdAndGenreId(bookId, genreId)).thenReturn(true);

    assertThatThrownBy(() -> genreService.addGenreToBook(bookId, genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            });
    verify(bookGenreRepository, never()).save(any());
  }

  @Test
  void testRemoveGenreFromBook_Success() {
    when(bookGenreRepository.existsByBookIdAndGenreId(bookId, genreId)).thenReturn(true);

    genreService.removeGenreFromBook(bookId, genreId);

    verify(bookGenreRepository).deleteByBookIdAndGenreId(bookId, genreId);
  }

  @Test
  void testRemoveGenreFromBook_NotFound() {
    when(bookGenreRepository.existsByBookIdAndGenreId(bookId, genreId)).thenReturn(false);

    assertThatThrownBy(() -> genreService.removeGenreFromBook(bookId, genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookGenreRepository, never()).deleteByBookIdAndGenreId(any(), any());
  }

  @Test
  void testGetGenresByBook() {
    BookGenre bookGenre = BookGenre.builder().bookId(bookId).genre(createSampleGenre()).build();
    when(bookGenreRepository.findByBookIdWithGenre(bookId)).thenReturn(List.of(bookGenre));

    List<GenreResponse> responses = genreService.getGenresByBook(bookId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).name()).isEqualTo("Fantasy");
    verify(bookGenreRepository).findByBookIdWithGenre(bookId);
  }

  @Test
  void testGetBooksByGenre_Success() {
    BookGenre bookGenre = BookGenre.builder().bookId(bookId).genre(createSampleGenre()).build();
    when(genreRepository.existsById(genreId)).thenReturn(true);
    when(bookGenreRepository.findByGenreId(genreId)).thenReturn(List.of(bookGenre));

    List<UUID> bookIds = genreService.getBooksByGenre(genreId);

    assertThat(bookIds).containsExactly(bookId);
    verify(genreRepository).existsById(genreId);
    verify(bookGenreRepository).findByGenreId(genreId);
  }

  @Test
  void testGetBooksByGenre_GenreNotFound() {
    when(genreRepository.existsById(genreId)).thenReturn(false);

    assertThatThrownBy(() -> genreService.getBooksByGenre(genreId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookGenreRepository, never()).findByGenreId(any());
  }
}
