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

  private final UUID authorId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private Author author;
  private Book book;

  @BeforeEach
  void setUp() {
    author = Author.builder().id(authorId).firstName("John").lastName("Doe").sexe(Sexe.M).build();

    book = new Book();
    book.setId(bookId);
    book.setTitle("Sample Book");
    book.setAuthors(new ArrayList<>());
  }

  @Test
  void testCreate_Success() {
    AuthorCreateRequest request = new AuthorCreateRequest("John", "Doe", Sexe.M);
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponse response = authorService.create(request);

    assertThat(response).isNotNull();
    assertThat(response.firstName()).isEqualTo("John");
    verify(authorRepository).save(any(Author.class));
  }

  @Test
  void testUpdate_Success() {
    AuthorUpdateRequest request = new AuthorUpdateRequest("Jane", "Doe");
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponse response = authorService.update(authorId, request);

    assertThat(response).isNotNull();
    verify(authorRepository).save(author);
  }

  @Test
  void testUpdate_NotFound() {
    AuthorUpdateRequest request = new AuthorUpdateRequest("Jane", "Doe");
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.update(authorId, request))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testAddAuthorToBook_Success() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    authorService.addAuthorToBook(bookId, authorId);

    assertThat(book.getAuthors()).contains(author);
    verify(bookRepository).save(book);
  }

  @Test
  void testRemoveAuthorFromBook_Success() {
    book.getAuthors().add(author);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    authorService.removeAuthorFromBook(bookId, authorId);

    assertThat(book.getAuthors()).doesNotContain(author);
    verify(bookRepository).save(book);
  }

  @Test
  void testRemoveAuthorFromBook_AssociationNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    assertThatThrownBy(() -> authorService.removeAuthorFromBook(bookId, authorId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void testGetAuthorsByBook() {
    book.getAuthors().add(author);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    List<AuthorResponse> responses = authorService.getAuthorsByBook(bookId);

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).firstName()).isEqualTo("John");
  }

  @Test
  void testGetBooksByAuthor_Success() {
    // 1. L'auteur doit être associé au livre pour passer le filtre .contains(author)
    book.getAuthors().add(author);

    // 2. Mocker le comportement réel utilisé dans AuthorService
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<UUID> bookIds = authorService.getBooksByAuthor(authorId);

    // 3. Assertions et vérifications
    assertThat(bookIds).containsExactly(bookId);
    verify(authorRepository).findById(authorId);
    verify(bookRepository).findAll();
  }
}
