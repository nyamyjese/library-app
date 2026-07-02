package com.hei.school.service;

import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.entity.Author;
import com.hei.school.entity.Book;
import com.hei.school.repository.AuthorRepository;
import com.hei.school.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorService {

  private final AuthorRepository authorRepository;
  private final BookRepository bookRepository;

  @Transactional
  public AuthorResponse create(AuthorCreateRequest request) {
    Author author =
        Author.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .sexe(request.sexe())
            .build();
    return toResponse(authorRepository.save(author));
  }

  @Transactional
  public AuthorResponse update(UUID id, AuthorUpdateRequest request) {
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Author not found: " + id));
    author.setFirstName(request.firstName());
    author.setLastName(request.lastName());
    return toResponse(authorRepository.save(author));
  }

  @Transactional
  public void delete(UUID id) {
    if (!authorRepository.existsById(id)) {
      throw new EntityNotFoundException("Author not found: " + id);
    }
    authorRepository.deleteById(id);
  }

  public AuthorResponse getById(UUID id) {
    return authorRepository
        .findById(id)
        .map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Author not found: " + id));
  }

  public List<AuthorResponse> getAll() {
    return authorRepository.findAll().stream().map(this::toResponse).toList();
  }

  public List<AuthorResponse> searchByLastName(String lastName) {
    return authorRepository.findByLastNameContainingIgnoreCase(lastName).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public void addAuthorToBook(UUID bookId, UUID authorId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));
    Author author =
        authorRepository
            .findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Author not found: " + authorId));

    if (!book.getAuthors().contains(author)) {
      book.getAuthors().add(author);
      bookRepository.save(book);
    }
  }

  @Transactional
  public void removeAuthorFromBook(UUID bookId, UUID authorId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));
    Author author =
        authorRepository
            .findById(authorId)
            .orElseThrow(() -> new EntityNotFoundException("Author not found: " + authorId));

    if (book.getAuthors().remove(author)) {
      bookRepository.save(book);
    } else {
      throw new EntityNotFoundException(
          "Association not found for book " + bookId + " and author " + authorId);
    }
  }

  public List<AuthorResponse> getAuthorsByBook(UUID bookId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));
    return book.getAuthors().stream().map(this::toResponse).toList();
  }

  public List<UUID> getBooksByAuthor(UUID authorId) {
    return bookRepository.findBookIdsByAuthorId(authorId);
  }

  private AuthorResponse toResponse(Author author) {
    return new AuthorResponse(
        author.getId(), author.getFirstName(), author.getLastName(), author.getSexe());
  }
}
