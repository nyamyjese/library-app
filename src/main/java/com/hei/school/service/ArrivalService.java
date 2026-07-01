package com.hei.school.service;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.request.UpdateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.mapper.ArrivalMapper;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ArrivalService {

  private final ArrivalRepository arrivalRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ArrivalMapper arrivalMapper;

  @Transactional
  public ArrivalResponse createArrival(CreateArrivalRequest request) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(request.bookCopyId())
            .orElseThrow(
                () -> new RuntimeException("BookCopy not found: id=" + request.bookCopyId()));

    // Met le BookCopy en AVAILABLE après arrivage
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);
    bookCopyRepository.save(bookCopy);

    Arrival arrival = arrivalMapper.toEntity(request, bookCopy);
    return arrivalMapper.toResponse(arrivalRepository.save(arrival));
  }

  @Transactional
  public ArrivalResponse updateArrival(UUID id, UpdateArrivalRequest request) {
    Arrival arrival =
        arrivalRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival not found: id=" + id));

    arrival.setQuantity(request.quantity());
    arrival.setUnitPrice(request.unitPrice());
    arrival.setArrivalDate(request.arrivalDate());

    return arrivalMapper.toResponse(arrivalRepository.save(arrival));
  }

  public ArrivalResponse getById(UUID id) {
    return arrivalMapper.toResponse(
        arrivalRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival not found: id=" + id)));
  }

  public List<ArrivalResponse> getAll() {
    return arrivalRepository.findAll().stream().map(arrivalMapper::toResponse).toList();
  }

  public List<ArrivalResponse> getByBookCopyId(UUID bookCopyId) {
    return arrivalRepository.findAllByBookCopy_Id(bookCopyId).stream()
        .map(arrivalMapper::toResponse)
        .toList();
  }

  public List<ArrivalResponse> getByBookId(UUID bookId) {
    return arrivalRepository.findAllByBookCopy_Book_Id(bookId).stream()
        .map(arrivalMapper::toResponse)
        .toList();
  }
}
