package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.response.BookCopyStockResponse;
import com.hei.school.dto.response.BookStockStatusResponse;
import com.hei.school.dto.response.LowStockResponse;
import com.hei.school.service.StockService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/stock")
public class StockController {

  private final StockService stockService;

  @GetMapping("/books")
  public List<BookStockStatusResponse> getAllBooksStock() {
    return stockService.getAllBooksStock();
  }

  @GetMapping("/books/{bookId}")
  public BookStockStatusResponse getStockByBook(@PathVariable UUID bookId) {
    return stockService.getStockByBook(bookId);
  }

  @GetMapping("/editions/{isbn}")
  public BookCopyStockResponse getStockByIsbn(@PathVariable String isbn) {
    return stockService.getStockByIsbn(isbn);
  }

  @GetMapping("/low")
  public List<LowStockResponse> getLowStock(@RequestParam(defaultValue = "3") long threshold) {
    return stockService.getLowStockBooks(threshold);
  }
}
