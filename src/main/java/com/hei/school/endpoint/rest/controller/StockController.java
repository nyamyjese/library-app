package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.StockDTO;
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
    public List<StockDTO> getStockByBook() {
        return stockService.getStockByBook();
    }

    @GetMapping("/books/{bookId}")
    public StockDTO getStockByBook(@PathVariable UUID bookId) {
        return stockService.getStockByBookId(bookId);
    }

    @GetMapping("/libraries")
    public List<StockDTO> getStockByLibrary() {
        return stockService.getStockByLibrary();
    }

    @GetMapping("/libraries/{libraryId}")
    public StockDTO getStockByLibrary(@PathVariable UUID libraryId) {
        return stockService.getStockByLibraryId(libraryId);
    }
}