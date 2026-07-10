package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.entity.Author;
import com.hei.school.entity.Book;
import com.hei.school.entity.enums.Sexe;
import com.hei.school.exception.NotFoundException;
import com.hei.school.repository.AuthorRepository;
import com.hei.school.repository.BookRepository;
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
class AuthorServiceTest {

  @Mock private AuthorRepository authorRepository;

  @Mock private BookRepository bookRepository;

  @InjectMocks private AuthorService authorService;

  private UUID authorId;
  private UUID bookId;
  private Author author;
  private Book book;

  @BeforeEach
  void setUp() {
    authorId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    author = Author.builder().id(authorId).firstName("John").lastName("Doe").sexe(Sexe.M).build();
    book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");
    book.setAuthors(new ArrayList<>());
  }

  @Test
  void create_shouldSaveAndReturnAuthorResponse() {
    AuthorCreateRequest request = new AuthorCreateRequest("John", "Doe", Sexe.M);
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponse response = authorService.create(request);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(authorId);
    assertThat(response.firstName()).isEqualTo("John");
    assertThat(response.lastName()).isEqualTo("Doe");
    assertThat(response.sexe()).isEqualTo(Sexe.M);
    verify(authorRepository).save(any(Author.class));
  }

  @Test
  void update_shouldUpdateAndReturnAuthorResponse_whenAuthorExists() {
    UUID id = authorId;
    AuthorUpdateRequest request = new AuthorUpdateRequest("Jane", "Smith");
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponse response = authorService.update(id, request);

    assertThat(response.firstName()).isEqualTo("Jane");
    assertThat(response.lastName()).isEqualTo("Smith");
    verify(authorRepository).findById(id);
    verify(authorRepository).save(author);
  }

  @Test
  void update_shouldThrowNotFoundException_whenAuthorNotFound() {
    UUID id = UUID.randomUUID();
    AuthorUpdateRequest request = new AuthorUpdateRequest("Jane", "Smith");
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.update(id, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Author not found: " + id);
    verify(authorRepository, never()).save(any());
  }

  @Test
  void delete_shouldDelete_whenAuthorExists() {
    when(authorRepository.existsById(authorId)).thenReturn(true);
    authorService.delete(authorId);
    verify(authorRepository).deleteById(authorId);
  }

  @Test
  void delete_shouldThrowNotFoundException_whenAuthorNotFound() {
    UUID id = UUID.randomUUID();
    when(authorRepository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> authorService.delete(id))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Author not found: " + id);
    verify(authorRepository, never()).deleteById(any());
  }

  @Test
  void getById_shouldReturnAuthorResponse_whenExists() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    AuthorResponse response = authorService.getById(authorId);

    assertThat(response.id()).isEqualTo(authorId);
    assertThat(response.firstName()).isEqualTo("John");
    verify(authorRepository).findById(authorId);
  }

  @Test
  void getById_shouldThrowNotFoundException_whenNotFound() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.getById(id))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Author not found: " + id);
  }

  @Test
  void getAll_shouldReturnListOfAuthorResponses() {
    Author author2 =
        Author.builder()
            .id(UUID.randomUUID())
            .firstName("Alice")
            .lastName("Brown")
            .sexe(Sexe.F)
            .build();
    when(authorRepository.findAll()).thenReturn(List.of(author, author2));

    List<AuthorResponse> responses = authorService.getAll();

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).firstName()).isEqualTo("John");
    assertThat(responses.get(1).firstName()).isEqualTo("Alice");
    verify(authorRepository).findAll();
  }

  @Test
  void searchByLastName_shouldReturnFilteredList() {
    String lastName = "Doe";
    when(authorRepository.findByLastNameContainingIgnoreCase(lastName)).thenReturn(List.of(author));

    List<AuthorResponse> responses = authorService.searchByLastName(lastName);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).lastName()).isEqualTo("Doe");
    verify(authorRepository).findByLastNameContainingIgnoreCase(lastName);
  }

  @Test
  void addAuthorToBook_shouldAddAuthor_whenNotAlreadyAssociated() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    authorService.addAuthorToBook(bookId, authorId);

    assertThat(book.getAuthors()).contains(author);
    verify(bookRepository).save(book);
  }

  @Test
  void addAuthorToBook_shouldDoNothing_whenAlreadyAssociated() {
    book.getAuthors().add(author);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    authorService.addAuthorToBook(bookId, authorId);

    assertThat(book.getAuthors()).hasSize(1);
    verify(bookRepository, never()).save(book);
  }

  @Test
  void addAuthorToBook_shouldThrowNotFoundException_whenBookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.addAuthorToBook(bookId, authorId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found: " + bookId);
    verify(bookRepository, never()).save(any());
  }

  @Test
  void addAuthorToBook_shouldThrowNotFoundException_whenAuthorNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.addAuthorToBook(bookId, authorId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Author not found: " + authorId);
    verify(bookRepository, never()).save(any());
  }

  @Test
  void removeAuthorFromBook_shouldRemoveAuthor_whenAssociated() {
    book.getAuthors().add(author);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    authorService.removeAuthorFromBook(bookId, authorId);

    assertThat(book.getAuthors()).doesNotContain(author);
    verify(bookRepository).save(book);
  }

  @Test
  void removeAuthorFromBook_shouldThrowNotFoundException_whenNotAssociated() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    assertThatThrownBy(() -> authorService.removeAuthorFromBook(bookId, authorId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Association not found");
    verify(bookRepository, never()).save(any());
  }

  @Test
  void removeAuthorFromBook_shouldThrowNotFoundException_whenBookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.removeAuthorFromBook(bookId, authorId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found: " + bookId);
  }

  @Test
  void getAuthorsByBook_shouldReturnListOfAuthors() {
    book.getAuthors().add(author);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    List<AuthorResponse> responses = authorService.getAuthorsByBook(bookId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).id()).isEqualTo(authorId);
    verify(bookRepository).findById(bookId);
  }

  @Test
  void getAuthorsByBook_shouldThrowNotFoundException_whenBookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.getAuthorsByBook(bookId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book not found: " + bookId);
  }

  @Test
  void getBooksByAuthor_shouldReturnListOfBookIds() {
      Book book2 = new Book();
      book2.setId(UUID.randomUUID());
      book2.setAuthors(List.of(author));
      when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
      when(bookRepository.findAll()).thenReturn(List.of(book, book2));

      List<UUID> bookIds = authorService.getBooksByAuthor(authorId);

      assertThat(bookIds).containsExactly(book2.getId());
      verify(authorRepository).findById(authorId);
      verify(bookRepository).findAll();
  }

  @Test
  void getBooksByAuthor_shouldThrowNotFoundException_whenAuthorNotFound() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.getBooksByAuthor(authorId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Author not found: " + authorId);
  }
}
