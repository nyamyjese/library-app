package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.request.LowStockResponse;
import com.hei.school.dto.request.UpdateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.dto.response.BookStockResponse;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.service.BookCopyService;
import com.hei.school.service.StockMovementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book-copies")
@RequiredArgsConstructor
public class BookCopyController {

  private final BookCopyService bookCopyService;
  private final StockMovementService stockMovementService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookCopyResponse create(@RequestBody @Valid CreateBookCopyRequest request) {
    return bookCopyService.create(request);
  }

  @GetMapping
  public List<BookCopyResponse> getAll(
      @RequestParam(required = false) UUID bookId,
      @RequestParam(required = false) UUID libraryId,
      @RequestParam(required = false) BookCopyStatus status) {
    return bookCopyService.getAll(bookId, libraryId, status);
  }

  @GetMapping("/{id}")
  public BookCopyResponse getById(@PathVariable UUID id) {
    return bookCopyService.getById(id);
  }

  @PutMapping("/{id}")
  public BookCopyResponse update(
      @PathVariable UUID id, @RequestBody @Valid UpdateBookCopyRequest request) {
    return bookCopyService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    bookCopyService.delete(id);
  }

  @GetMapping("/{bookCopyId}/stock")
  public BookStockResponse getStock(@PathVariable UUID bookCopyId) {
    return stockMovementService.getStockByBookCopyId(bookCopyId);
  }

  @GetMapping("/low-stock")
  public List<LowStockResponse> getLowStock(@RequestParam(required = false) Integer threshold) {
    return bookCopyService.getLowStock(threshold);
  }
}
