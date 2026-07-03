package com.hei.school.service;

import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.Genre;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.GenreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;
  private final BookRepository bookRepository;

  @Transactional
  public GenreResponse createGenre(GenreCreateRequest request) {
    Genre genre = Genre.builder().name(request.name()).build();
    Genre saved = genreRepository.save(genre);
    return mapToResponse(saved);
  }

  @Transactional
  public GenreResponse updateGenre(UUID id, GenreUpdateRequest request) {
    Genre genre =
        genreRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
    genre.setName(request.name());
    return mapToResponse(genreRepository.save(genre));
  }

  @Transactional
  public void deleteGenre(UUID genreId) {
    Genre genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
    genreRepository.delete(genre);
  }

  public GenreResponse getGenreById(UUID genreId) {
    Genre genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
    return mapToResponse(genre);
  }

  public List<GenreResponse> getAllGenres() {
    return genreRepository.findAll().stream().map(this::mapToResponse).toList();
  }

  public List<GenreResponse> searchGenresByName(String name) {
    return genreRepository.findByNameContainingIgnoreCase(name).stream()
        .map(this::mapToResponse)
        .toList();
  }

  @Transactional
  public void addGenreToBook(UUID bookId, UUID genreId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    Genre genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

    if (book.getGenres() == null) {
      book.setGenres(new ArrayList<>());
    }

    if (book.getGenres().contains(genre)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Association already exists");
    }

    book.getGenres().add(genre);
    bookRepository.save(book);
  }

  @Transactional
  public void removeGenreFromBook(UUID bookId, UUID genreId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    Genre genre =
        genreRepository
            .findById(genreId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

    if (book.getGenres() == null || !book.getGenres().remove(genre)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Association not found");
    }
    bookRepository.save(book);
  }

  public List<GenreResponse> getGenresByBook(UUID bookId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

    if (book.getGenres() == null) {
      return List.of();
    }
    return book.getGenres().stream().map(this::mapToResponse).toList();
  }

  public List<UUID> getBooksByGenre(UUID genreId) {
    if (!genreRepository.existsById(genreId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
    }
    return bookRepository.findAll().stream()
        .filter(
            book ->
                book.getGenres() != null
                    && book.getGenres().stream().anyMatch(g -> g.getId().equals(genreId)))
        .map(Book::getId)
        .toList();
  }

  private GenreResponse mapToResponse(Genre genre) {
    return new GenreResponse(genre.getId(), genre.getName());
  }
}
