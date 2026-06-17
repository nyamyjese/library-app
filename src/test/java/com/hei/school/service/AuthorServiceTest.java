package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.entity.Author;
import com.hei.school.entity.BookAuthor;
import com.hei.school.entity.enums.Sexe;
import com.hei.school.repository.AuthorRepository;
import com.hei.school.repository.BookAuthorRepository;
import com.hei.school.repository.BookRepository;
import java.time.Instant;
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
class AuthorServiceTest {

  @Mock private AuthorRepository authorRepository;

  @Mock private BookAuthorRepository bookAuthorRepository;

  @Mock private BookRepository bookRepository;

  @InjectMocks private AuthorService authorService;

  private final UUID authorId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final Author author =
      Author.builder().idAuthor(authorId).firstName("John").lastName("Doe").build();

  @Test
  void testCreateAuthor_Success() {
    AuthorCreateRequest request = new AuthorCreateRequest("Jane", "Smith", Sexe.M, Instant.now());
    Author savedAuthor =
        Author.builder().idAuthor(UUID.randomUUID()).firstName("Jane").lastName("Smith").build();
    when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

    AuthorResponse response = authorService.createAuthor(request);

    assertThat(response.idAuthor()).isEqualTo(savedAuthor.getIdAuthor());
    assertThat(response.firstName()).isEqualTo("Jane");
    assertThat(response.lastName()).isEqualTo("Smith");
    verify(authorRepository).save(any(Author.class));
  }

  @Test
  void testUpdateAuthor_Success() {
    AuthorUpdateRequest request = new AuthorUpdateRequest(authorId, "John", "DoeUpdated");
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponse response = authorService.updateAuthor(request);

    assertThat(response.lastName()).isEqualTo("DoeUpdated");
    verify(authorRepository).findById(authorId);
    verify(authorRepository).save(author);
  }

  @Test
  void testUpdateAuthor_NotFound() {
    AuthorUpdateRequest request = new AuthorUpdateRequest(authorId, "John", "Doe");
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.updateAuthor(request))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
              assertThat(rse.getReason()).isEqualTo("Author not found");
            });
    verify(authorRepository, never()).save(any());
  }

  @Test
  void testDeleteAuthor_Success() {
    List<BookAuthor> associations = List.of(BookAuthor.builder().build());
    when(authorRepository.existsById(authorId)).thenReturn(true);
    when(bookAuthorRepository.findByAuthorId(authorId)).thenReturn(associations);

    authorService.deleteAuthor(authorId);

    verify(bookAuthorRepository).deleteAll(associations);
    verify(authorRepository).deleteById(authorId);
  }

  @Test
  void testDeleteAuthor_NotFound() {
    when(authorRepository.existsById(authorId)).thenReturn(false);

    assertThatThrownBy(() -> authorService.deleteAuthor(authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookAuthorRepository, never()).deleteAll(any());
    verify(authorRepository, never()).deleteById(any());
  }

  @Test
  void testGetAuthorById_Success() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    AuthorResponse response = authorService.getAuthorById(authorId);

    assertThat(response.idAuthor()).isEqualTo(authorId);
    assertThat(response.firstName()).isEqualTo("John");
    verify(authorRepository).findById(authorId);
  }

  @Test
  void testGetAuthorById_NotFound() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.getAuthorById(authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
  }

  @Test
  void testGetAllAuthors() {
    when(authorRepository.findAll()).thenReturn(List.of(author));

    List<AuthorResponse> responses = authorService.getAllAuthors();

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).firstName()).isEqualTo("John");
    verify(authorRepository).findAll();
  }

  @Test
  void testSearchAuthorsByLastName() {
    String lastName = "Doe";
    when(authorRepository.findByLastNameContainingIgnoreCase(lastName)).thenReturn(List.of(author));

    List<AuthorResponse> responses = authorService.searchAuthorsByLastName(lastName);

    assertThat(responses).hasSize(1);
    verify(authorRepository).findByLastNameContainingIgnoreCase(lastName);
  }

  @Test
  void testAddAuthorToBook_Success() {
    when(authorRepository.existsById(authorId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true); // Changé ici
    when(bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)).thenReturn(false);
    when(authorRepository.getReferenceById(authorId)).thenReturn(author);

    authorService.addAuthorToBook(bookId, authorId);

    verify(bookAuthorRepository).save(any(BookAuthor.class));
  }

  @Test
  void testAddAuthorToBook_AuthorNotFound() {
    when(authorRepository.existsById(authorId)).thenReturn(false);

    assertThatThrownBy(() -> authorService.addAuthorToBook(bookId, authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookAuthorRepository, never()).save(any());
  }

  @Test
  void testAddAuthorToBook_BookNotFound() {
    when(authorRepository.existsById(authorId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(false); // Changé ici

    assertThatThrownBy(() -> authorService.addAuthorToBook(bookId, authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
              assertThat(rse.getReason()).isEqualTo("Book not found");
            });
    verify(bookAuthorRepository, never()).save(any());
  }

  @Test
  void testAddAuthorToBook_AlreadyExists() {
    when(authorRepository.existsById(authorId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true); // Changé ici
    when(bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)).thenReturn(true);

    assertThatThrownBy(() -> authorService.addAuthorToBook(bookId, authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            });
    verify(bookAuthorRepository, never()).save(any());
  }

  @Test
  void testRemoveAuthorFromBook_Success() {
    when(bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)).thenReturn(true);

    authorService.removeAuthorFromBook(bookId, authorId);

    verify(bookAuthorRepository).deleteByBookIdAndAuthorId(bookId, authorId);
  }

  @Test
  void testRemoveAuthorFromBook_NotFound() {
    when(bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)).thenReturn(false);

    assertThatThrownBy(() -> authorService.removeAuthorFromBook(bookId, authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookAuthorRepository, never()).deleteByBookIdAndAuthorId(any(), any());
  }

  @Test
  void testGetAuthorsByBook() {
    BookAuthor bookAuthor = BookAuthor.builder().bookId(bookId).author(author).build();
    when(bookAuthorRepository.findByBookIdWithAuthor(bookId)).thenReturn(List.of(bookAuthor));

    List<AuthorResponse> responses = authorService.getAuthorsByBook(bookId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).firstName()).isEqualTo("John");
    verify(bookAuthorRepository).findByBookIdWithAuthor(bookId);
  }

  @Test
  void testGetBooksByAuthor_Success() {
    BookAuthor bookAuthor = BookAuthor.builder().bookId(bookId).author(author).build();
    when(authorRepository.existsById(authorId)).thenReturn(true);
    when(bookAuthorRepository.findByAuthorId(authorId)).thenReturn(List.of(bookAuthor));

    List<UUID> bookIds = authorService.getBooksByAuthor(authorId);

    assertThat(bookIds).containsExactly(bookId);
    verify(authorRepository).existsById(authorId);
    verify(bookAuthorRepository).findByAuthorId(authorId);
  }

  @Test
  void testGetBooksByAuthor_AuthorNotFound() {
    when(authorRepository.existsById(authorId)).thenReturn(false);

    assertThatThrownBy(() -> authorService.getBooksByAuthor(authorId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex -> {
              ResponseStatusException rse = (ResponseStatusException) ex;
              assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    verify(bookAuthorRepository, never()).findByAuthorId(any());
  }
}
