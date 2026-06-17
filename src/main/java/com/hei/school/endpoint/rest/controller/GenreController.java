package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.service.GenreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  @PostMapping
  public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody GenreCreateRequest request) {
    GenreResponse response = genreService.createGenre(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping
  public ResponseEntity<GenreResponse> updateGenre(@Valid @RequestBody GenreUpdateRequest request) {
    GenreResponse response = genreService.updateGenre(request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{genreId}")
  public ResponseEntity<Void> deleteGenre(@PathVariable UUID genreId) {
    genreService.deleteGenre(genreId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{genreId}")
  public ResponseEntity<GenreResponse> getGenreById(@PathVariable UUID genreId) {
    GenreResponse response = genreService.getGenreById(genreId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<GenreResponse>> getAllGenres(
      @RequestParam(name = "name", required = false) String name) {
    if (name != null && !name.isBlank()) {
      return ResponseEntity.ok(genreService.searchGenresByName(name));
    }
    return ResponseEntity.ok(genreService.getAllGenres());
  }

  @PostMapping("/{genreId}/books/{bookId}")
  public ResponseEntity<Void> addGenreToBook(
      @PathVariable UUID genreId, @PathVariable UUID bookId) {
    genreService.addGenreToBook(bookId, genreId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{genreId}/books/{bookId}")
  public ResponseEntity<Void> removeGenreFromBook(
      @PathVariable UUID genreId, @PathVariable UUID bookId) {
    genreService.removeGenreFromBook(bookId, genreId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/books/{bookId}/genres")
  public ResponseEntity<List<GenreResponse>> getGenresByBook(@PathVariable UUID bookId) {
    return ResponseEntity.ok(genreService.getGenresByBook(bookId));
  }

  @GetMapping("/{genreId}/books")
  public ResponseEntity<List<UUID>> getBooksByGenre(@PathVariable UUID genreId) {
    return ResponseEntity.ok(genreService.getBooksByGenre(genreId));
  }
}
