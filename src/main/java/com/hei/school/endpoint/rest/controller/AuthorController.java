package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.service.AuthorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  @GetMapping
  public List<AuthorResponse> getAll() {
    return authorService.getAll();
  }

  @GetMapping("/{id}")
  public AuthorResponse getById(@PathVariable UUID id) {
    return authorService.getById(id);
  }

  @GetMapping("/search")
  public List<AuthorResponse> searchByLastName(@RequestParam String lastName) {
    return authorService.searchByLastName(lastName);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AuthorResponse create(@Valid @RequestBody AuthorCreateRequest request) {
    return authorService.create(request);
  }

  @PutMapping("/{id}")
  public AuthorResponse update(
      @PathVariable UUID id, @Valid @RequestBody AuthorUpdateRequest request) {
    return authorService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    authorService.delete(id);
  }

  @PostMapping("/{authorId}/books/{bookId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addAuthorToBook(@PathVariable UUID authorId, @PathVariable UUID bookId) {
    authorService.addAuthorToBook(bookId, authorId);
  }

  @DeleteMapping("/{authorId}/books/{bookId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeAuthorFromBook(@PathVariable UUID authorId, @PathVariable UUID bookId) {
    authorService.removeAuthorFromBook(bookId, authorId);
  }

  @GetMapping("/{authorId}/books")
  public List<UUID> getBooksByAuthor(@PathVariable UUID authorId) {
    return authorService.getBooksByAuthor(authorId);
  }

  @GetMapping("/books/{bookId}/authors")
  public List<AuthorResponse> getAuthorsByBook(@PathVariable UUID bookId) {
    return authorService.getAuthorsByBook(bookId);
  }
}
