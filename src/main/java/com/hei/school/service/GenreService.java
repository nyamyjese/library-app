package com.hei.school.service;

import com.hei.school.client.BookClient;
import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.entity.BookGenre;
import com.hei.school.entity.Genre;
import com.hei.school.repository.BookGenreRepository;
import com.hei.school.repository.GenreRepository;
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
public class GenreService {

  private final GenreRepository genreRepository;
  private final BookGenreRepository bookGenreRepository;
  private final BookClient bookClient;

  @Transactional
  public GenreResponse createGenre(GenreCreateRequest request) {
    Genre genre = Genre.builder().name(request.name()).build();
    Genre saved = genreRepository.save(genre);
    return mapToResponse(saved);
  }

  @Transactional
  public GenreResponse updateGenre(GenreUpdateRequest request) {
    Genre genre =
        genreRepository
            .findById(request.id())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
    genre.setName(request.name());
    Genre updated = genreRepository.save(genre);
    return mapToResponse(updated);
  }

  @Transactional
  public void deleteGenre(UUID genreId) {
    if (!genreRepository.existsById(genreId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
    }
    List<BookGenre> associations = bookGenreRepository.findByGenreId(genreId);
    bookGenreRepository.deleteAll(associations);
    genreRepository.deleteById(genreId);
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
    return genreRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  public List<GenreResponse> searchGenresByName(String name) {
    return genreRepository.findByNameContainingIgnoreCase(name).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public void addGenreToBook(UUID bookId, UUID genreId) {
    if (!genreRepository.existsById(genreId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
    }
    if (!bookClient.bookExists(bookId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
    }
    if (bookGenreRepository.existsByBookIdAndGenreId(bookId, genreId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Association already exists");
    }
    BookGenre bookGenre =
        BookGenre.builder().bookId(bookId).genre(genreRepository.getReferenceById(genreId)).build();
    bookGenreRepository.save(bookGenre);
  }

  @Transactional
  public void removeGenreFromBook(UUID bookId, UUID genreId) {
    if (!bookGenreRepository.existsByBookIdAndGenreId(bookId, genreId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Association not found");
    }
    bookGenreRepository.deleteByBookIdAndGenreId(bookId, genreId);
  }

  public List<GenreResponse> getGenresByBook(UUID bookId) {
    List<BookGenre> bookGenres = bookGenreRepository.findByBookIdWithGenre(bookId);
    return bookGenres.stream()
        .map(BookGenre::getGenre)
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public List<UUID> getBooksByGenre(UUID genreId) {
    if (!genreRepository.existsById(genreId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
    }
    List<BookGenre> bookGenres = bookGenreRepository.findByGenreId(genreId);
    return bookGenres.stream().map(BookGenre::getBookId).collect(Collectors.toList());
  }

  private GenreResponse mapToResponse(Genre genre) {
    return new GenreResponse(genre.getId(), genre.getName());
  }
}