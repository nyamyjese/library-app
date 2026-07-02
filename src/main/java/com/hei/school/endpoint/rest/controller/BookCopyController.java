package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.enums.BookCopyStatus;
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
    public BookCopyDTO getById(@PathVariable UUID id) {
        return bookCopyService.getById(id);
    }

    @GetMapping("/available")
    public List<BookCopyDTO> getAvailable() {
        return bookCopyService.getAvailable();
    }

    @GetMapping(params = "bookId")
    public List<BookCopyDTO> getByBook(@RequestParam UUID bookId) {
        return bookCopyService.getByBook(bookId);
    }

    @GetMapping(value = "/available", params = "bookId")
    public List<BookCopyDTO> getAvailableByBook(@RequestParam UUID bookId) {
        return bookCopyService.getAvailableByBook(bookId);
    }

    @GetMapping(params = "libraryId")
    public List<BookCopyDTO> getByLibrary(@RequestParam UUID libraryId) {
        return bookCopyService.getByLibrary(libraryId);
    }

    @GetMapping("/count/available")
    public long countAvailableByBook(@RequestParam UUID bookId) {
        return bookCopyService.countAvailableByBook(bookId);
    }

    @PatchMapping("/{id}/status")
    public BookCopyDTO updateStatus(@PathVariable UUID id, @RequestParam BookCopyStatus status) {
        return bookCopyService.updateStatus(id, status);
    }
}