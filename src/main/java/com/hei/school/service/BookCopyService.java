package com.hei.school.service;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.mapper.BookCopyMapper;
import com.hei.school.repository.BookCopyRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookCopyMapper bookCopyMapper;

    public BookCopyDTO getById(UUID id) {
        return bookCopyMapper.toDTO(bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookCopy not found: id=" + id)));
    }

    public List<BookCopyDTO> getAll() {
        return bookCopyRepository.findAll().stream()
                .map(bookCopyMapper::toDTO)
                .toList();
    }

    public List<BookCopyDTO> getAvailable() {
        return bookCopyRepository.findAllByStatus(BookCopyStatus.AVAILABLE).stream()
                .map(bookCopyMapper::toDTO)
                .toList();
    }

    public List<BookCopyDTO> getByBook(UUID bookId) {
        return bookCopyRepository.findAllByBook_Id(bookId).stream()
                .map(bookCopyMapper::toDTO)
                .toList();
    }

    public List<BookCopyDTO> getAvailableByBook(UUID bookId) {
        return bookCopyRepository.findAllByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE).stream()
                .map(bookCopyMapper::toDTO)
                .toList();
    }

    public List<BookCopyDTO> getByLibrary(UUID libraryId) {
        return bookCopyRepository.findAllByLibrary_Id(libraryId).stream()
                .map(bookCopyMapper::toDTO)
                .toList();
    }

    public BookCopyDTO updateStatus(UUID id, BookCopyStatus newStatus) {
        BookCopy copy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookCopy not found: id=" + id));
        copy.setStatus(newStatus);
        return bookCopyMapper.toDTO(bookCopyRepository.save(copy));
    }

    public long countAvailableByBook(UUID bookId) {
        return bookCopyRepository.countByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE);
    }
}