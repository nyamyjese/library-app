package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.BookDTO;
import com.hei.school.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    @GetMapping("/search/isbn")
    public ResponseEntity<BookDTO> searchByIsbn(@RequestParam String isbn) {
        return ResponseEntity.ok(bookService.searchByIsbn(isbn));
    }

    @GetMapping("/library/{libraryId}")
    public ResponseEntity<List<BookDTO>> getByLibrary(@PathVariable Long libraryId) {
        return ResponseEntity.ok(bookService.getByLibrary(libraryId));
    }

    @GetMapping("/filter/year")
    public ResponseEntity<List<BookDTO>> getByYear(@RequestParam Integer year) {
        return ResponseEntity.ok(bookService.getByYear(year));
    }

    @GetMapping("/sort/title")
    public ResponseEntity<List<BookDTO>> sortedByTitle() {
        return ResponseEntity.ok(bookService.getAllSortedByTitle());
    }

    @GetMapping("/sort/price")
    public ResponseEntity<List<BookDTO>> sortedByPrice() {
        return ResponseEntity.ok(bookService.getAllSortedByPrice());
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable UUID id, @RequestBody BookDTO dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}