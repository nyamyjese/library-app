package com.hei.school.service;

import com.hei.school.dto.BookDTO;
import com.hei.school.entity.Book;
import com.hei.school.entity.Library;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.LibraryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  @Autowired private BookRepository bookRepository;

  @Autowired private LibraryRepository libraryRepository;

  public List<BookDTO> getAll() {
    return bookRepository.findAll().stream().map(this::toDTO).toList();
  }

  public BookDTO getById(UUID id) {
    return toDTO(
        bookRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Book with id : " + id + " not found!")));
  }

  public List<BookDTO> searchByTitle(String title) {
    return bookRepository.findByTitleContainingIgnoreCase(title).stream().map(this::toDTO).toList();
  }

  public BookDTO searchByIsbn(String isbn) {
    return toDTO(
        bookRepository
            .findByIsbn(isbn)
            .orElseThrow(() -> new RuntimeException("Book with ISBN : " + isbn + " not found!")));
  }

  public List<BookDTO> getByLibrary(UUID libraryId) {
    return bookRepository.findByLibrary_LibraryId(libraryId).stream().map(this::toDTO).toList();
  }

  public List<BookDTO> getByYear(Integer year) {
    return bookRepository.findByPublicationYear(year).stream().map(this::toDTO).toList();
  }

  public List<BookDTO> getAllSortedByTitle() {
    return bookRepository.findAllByOrderByTitleAsc().stream().map(this::toDTO).toList();
  }

  public List<BookDTO> getAllSortedByPrice() {
    return bookRepository.findAllByOrderByPriceAsc().stream().map(this::toDTO).toList();
  }

  public BookDTO create(BookDTO dto) {
    Library library =
        libraryRepository
            .findById(dto.getLibraryId())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Library with id : " + dto.getLibraryId() + " not found!"));
    Book book = new Book();
    book.setTitle(dto.getTitle());
    book.setIsbn(dto.getIsbn());
    book.setPublicationYear(dto.getPublicationYear());
    book.setPrice(dto.getPrice());
    book.setLibrary(library);
    return toDTO(bookRepository.save(book));
  }

  public BookDTO update(UUID id, BookDTO dto) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Book with id : " + id + " not found!"));
    Library library =
        libraryRepository
            .findById(dto.getLibraryId())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Library with id : " + dto.getLibraryId() + " not found!"));
    book.setTitle(dto.getTitle());
    book.setIsbn(dto.getIsbn());
    book.setPublicationYear(dto.getPublicationYear());
    book.setPrice(dto.getPrice());
    book.setLibrary(library);
    return toDTO(bookRepository.save(book));
  }

  public void delete(UUID id) {
    bookRepository.deleteById(id);
  }

  private BookDTO toDTO(Book book) {
    BookDTO dto = new BookDTO();
    dto.setId(book.getId());
    dto.setTitle(book.getTitle());
    dto.setIsbn(book.getIsbn());
    dto.setPublicationYear(book.getPublicationYear());
    dto.setPrice(book.getPrice());
    dto.setLibraryId(book.getLibrary().getLibraryId());
    return dto;
  }
}
