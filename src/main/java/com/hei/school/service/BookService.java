package com.hei.school.service;

import com.hei.school.dto.BookDTO;
import com.hei.school.entity.Book;
import com.hei.school.entity.Library;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book with id: " + id + " not found"));
    }

    public List<Book> searchByTitle (String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public Book searchByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book with isbn : " + isbn +  " not found"));
    }

    public List<Book> getByLibraryId(Long libraryId) {
        return bookRepository.findByLibraryId(libraryId);
    }

    public List<Book> getByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }

    public List<Book> getAllSortedByTitle() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    public List<Book> getAllSortedByPrice(){
        return bookRepository.findAllByOrderByBasePriceAsc();
    }

    public Book create(BookDTO  bookDTO) {
        Library library = libraryRepository.findById(bookDTO.getLibraryId())
                .orElseThrow(() -> new RuntimeException("Library with id: " + bookDTO.getLibraryId() + " not found"));

        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationYear(bookDTO.getPublicationYear());
        book.setPrice(bookDTO.getPrice());
        book.setLibrary(library);
        return bookRepository.save(book);
    }

    public Book update(Long id, BookDTO  bookDTO) {
        Book book = getById(id);
        Library library = libraryRepository.findById(bookDTO.getLibraryId())
                .orElseThrow(() -> new RuntimeException("Library with id: " + bookDTO.getLibraryId() + " not found"));

        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationYear(bookDTO.getPublicationYear());
        book.setPrice(bookDTO.getPrice());
        book.setLibrary(library);
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
