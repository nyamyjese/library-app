package com.hei.school.service;

import com.hei.school.client.BookClient;
import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.entity.Author;
import com.hei.school.entity.BookAuthor;
import com.hei.school.repository.AuthorRepository;
import com.hei.school.repository.BookAuthorRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthorService {

  private final AuthorRepository authorRepository;
  private final BookAuthorRepository bookAuthorRepository;
  private final BookClient bookClient;

  @Transactional
  public AuthorResponse createAuthor(AuthorCreateRequest request) {
    Author author =
        Author.builder().firstName(request.firstName()).lastName(request.lastName()).build();
    Author saved = authorRepository.save(author);
    return toResponse(saved);
  }

  @Transactional
  public AuthorResponse updateAuthor(AuthorUpdateRequest request) {
    Author author =
        authorRepository
            .findById(request.id())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
    author.setFirstName(request.firstName());
    author.setLastName(request.lastName());
    Author updated = authorRepository.save(author);
    return toResponse(updated);
  }

  @Transactional
  public void deleteAuthor(UUID authorId) {
    if (!authorRepository.existsById(authorId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
    }
    List<BookAuthor> associations = bookAuthorRepository.findByAuthorId(authorId);
    bookAuthorRepository.deleteAll(associations);
    authorRepository.deleteById(authorId);
  }

  public AuthorResponse getAuthorById(UUID authorId) {
    Author author =
        authorRepository
            .findById(authorId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
    return toResponse(author);
  }

  public List<AuthorResponse> getAllAuthors() {
    return authorRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<AuthorResponse> searchAuthorsByLastName(String lastName) {
    return authorRepository.findByLastNameContainingIgnoreCase(lastName).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public void addAuthorToBook(UUID bookId, UUID authorId) {
    if (!authorRepository.existsById(authorId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
    }
    if (!bookClient.bookExists(bookId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
    }
    if (bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Association already exists");
    }
    BookAuthor ba =
        BookAuthor.builder()
            .bookId(bookId)
            .author(authorRepository.getReferenceById(authorId))
            .build();
    bookAuthorRepository.save(ba);
  }

  @Transactional
  public void removeAuthorFromBook(UUID bookId, UUID authorId) {
    if (!bookAuthorRepository.existsByBookIdAndAuthorId(bookId, authorId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Association not found");
    }
    bookAuthorRepository.deleteByBookIdAndAuthorId(bookId, authorId);
  }

  public List<AuthorResponse> getAuthorsByBook(UUID bookId) {
    List<BookAuthor> bookAuthors = bookAuthorRepository.findByBookIdWithAuthor(bookId);
    return bookAuthors.stream().map(ba -> toResponse(ba.getAuthor())).collect(Collectors.toList());
  }

  public List<UUID> getBooksByAuthor(UUID authorId) {
    if (!authorRepository.existsById(authorId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
    }
    List<BookAuthor> associations = bookAuthorRepository.findByAuthorId(authorId);
    return associations.stream().map(BookAuthor::getBookId).collect(Collectors.toList());
  }

  private AuthorResponse toResponse(Author author) {
    return new AuthorResponse(author.getIdAuthor(), author.getFirstName(), author.getLastName());
  }
}
