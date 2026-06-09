package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.BookDTO;
import com.hei.school.entity.Book;
import com.hei.school.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    @GetMapping("/search/isbn")
    public ResponseEntity<Book> searchByIsbn(@RequestParam String isbn) {
        return ResponseEntity.ok(bookService.searchByIsbn(isbn));
    }

    @GetMapping("/library/{libraryId}")
    public ResponseEntity<List<Book>> getByLibraryId(@PathVariable Long libraryId) {
        return ResponseEntity.ok(bookService.getByLibraryId(libraryId));
    }

    @GetMapping("/filter/year")
    public ResponseEntity<List<Book>> getByYear(@RequestParam Integer year) {
        return ResponseEntity.ok(bookService.getByPublicationYear(year));
    }

    @GetMapping("/sort/title")
    public ResponseEntity<List<Book>> sortedByTitle() {
        return ResponseEntity.ok(bookService.getAllSortedByTitle());
    }

    @GetMapping("/sort/price")
    public ResponseEntity<List<Book>> sortedByPrice() {
        return ResponseEntity.ok(bookService.getAllSortedByPrice());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(bookDTO));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.update(id, bookDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
