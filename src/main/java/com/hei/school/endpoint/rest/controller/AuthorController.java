package com.hei.school.endpoint.rest.controller;

<<<<<<< HEAD
<<<<<<< HEAD
import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.service.AuthorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  @PostMapping
  public ResponseEntity<AuthorResponse> createAuthor(
      @Valid @RequestBody AuthorCreateRequest request) {
    AuthorResponse response = authorService.createAuthor(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping
  public ResponseEntity<AuthorResponse> updateAuthor(
      @Valid @RequestBody AuthorUpdateRequest request) {
    AuthorResponse response = authorService.updateAuthor(request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{authorId}")
  public ResponseEntity<Void> deleteAuthor(@PathVariable UUID authorId) {
    authorService.deleteAuthor(authorId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{authorId}")
  public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable UUID authorId) {
    AuthorResponse response = authorService.getAuthorById(authorId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<AuthorResponse>> getAllAuthors(
      @RequestParam(name = "lastName", required = false) String lastName) {
    if (lastName != null && !lastName.isBlank()) {
      return ResponseEntity.ok(authorService.searchAuthorsByLastName(lastName));
    }
    return ResponseEntity.ok(authorService.getAllAuthors());
  }

  @PostMapping("/{authorId}/books/{bookId}")
  public ResponseEntity<Void> addAuthorToBook(
      @PathVariable UUID authorId, @PathVariable UUID bookId) {
    authorService.addAuthorToBook(bookId, authorId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{authorId}/books/{bookId}")
  public ResponseEntity<Void> removeAuthorFromBook(
      @PathVariable UUID authorId, @PathVariable UUID bookId) {
    authorService.removeAuthorFromBook(bookId, authorId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/books/{bookId}/authors")
  public ResponseEntity<List<AuthorResponse>> getAuthorsByBook(@PathVariable UUID bookId) {
    return ResponseEntity.ok(authorService.getAuthorsByBook(bookId));
  }

  @GetMapping("/{authorId}/books")
  public ResponseEntity<List<UUID>> getBooksByAuthor(@PathVariable UUID authorId) {
    return ResponseEntity.ok(authorService.getBooksByAuthor(authorId));
  }
}
