package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.CopyStatus;
import com.hei.school.service.BookCopyService;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/book-copies")
public class BookCopyController {

  private final BookCopyService bookCopyService;

  @GetMapping
  public List<BookCopyDTO> getAll() {
    return bookCopyService.getAll();
  }

  @GetMapping("/{id}")
  public BookCopyDTO getById(@PathVariable Integer id) {
    return bookCopyService.getById(id);
  }

  @GetMapping("/available")
  public List<BookCopyDTO> getAvailable() {
    return bookCopyService.getAvailable();
  }

  @GetMapping("/sold")
  public List<BookCopyDTO> getSold() {
    return bookCopyService.getSold();
  }

  @GetMapping(params = "bookId")
  public List<BookCopyDTO> getByBook(@RequestParam UUID bookId) {
    return bookCopyService.getByBook(bookId);
  }

  @GetMapping(value = "/available", params = "bookId")
  public List<BookCopyDTO> getAvailableByBook(@RequestParam Integer bookId) {
    return bookCopyService.getAvailableByBook(bookId);
  }

  @GetMapping(params = "libraryId")
  public List<BookCopyDTO> getByLibrary(@RequestParam Integer libraryId) {
    return bookCopyService.getByLibrary(libraryId);
  }

  @GetMapping(value = "/available", params = "libraryId")
  public List<BookCopyDTO> getAvailableByLibrary(@RequestParam Integer libraryId) {
    return bookCopyService.getAvailableByLibrary(libraryId);
  }

  @GetMapping("/count/available")
  public long countAvailableByBook(@RequestParam Integer bookId) {
    return bookCopyService.countAvailableByBook(bookId);
  }

  // Appelé par la Personne 4 pour marquer un exemplaire comme SOLD
  @PatchMapping("/{id}/status")
  public BookCopyDTO updateStatus(@PathVariable Integer id, @RequestParam CopyStatus status) {
    return bookCopyService.updateStatus(id, status);
  }
}
