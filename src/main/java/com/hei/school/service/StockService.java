package com.hei.school.service;

import com.hei.school.dto.StockDTO;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import com.hei.school.repository.BookCopyRepository;
import com.hei.school.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final BookCopyRepository bookCopyRepository;

    public List<StockDTO> getStockByBook() {
        List<UUID> bookIds = stockRepository.findDistinctBookIds();
        List<StockDTO> result = new ArrayList<>();
        for (UUID bookId : bookIds) {
            result.add(getStockByBookId(bookId));
        }
        return result;
    }

    public StockDTO getStockByBookId(UUID bookId) {
        List<BookCopy> copies = bookCopyRepository.findAllByBookId(bookId);
        return buildStock(copies, bookId, null);
    }

    public List<StockDTO> getStockByLibrary() {
        List<UUID> libraryIds = stockRepository.findDistinctLibraryIds();
        List<StockDTO> result = new ArrayList<>();
        for (UUID libraryId : libraryIds) {
            result.add(getStockByLibraryId(libraryId));
        }
        return result;
    }

    public StockDTO getStockByLibraryId(UUID libraryId) {
        List<BookCopy> copies = bookCopyRepository.findAllByLibraryId(libraryId);
        return buildStock(copies, null, libraryId);
    }

    private StockDTO buildStock(List<BookCopy> copies, UUID bookId, UUID libraryId) {
        long available = 0, sold = 0, damaged = 0, lost = 0;
        for (BookCopy c : copies) {
            if (c.getStatus() == CopyStatus.AVAILABLE) available++;
            else if (c.getStatus() == CopyStatus.SOLD) sold++;
            else if (c.getStatus() == CopyStatus.DAMAGED) damaged++;
            else if (c.getStatus() == CopyStatus.LOST) lost++;
        }
        return StockDTO.builder()
                .bookId(bookId)
                .libraryId(libraryId)
                .totalCopies(copies.size())
                .availableCopies(available)
                .soldCopies(sold)
                .damagedCopies(damaged)
                .lostCopies(lost)
                .build();
    }
}