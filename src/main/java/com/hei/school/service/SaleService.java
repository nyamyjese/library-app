package com.hei.school.service;

import com.hei.school.dto.request.CreateSaleRequest;
import com.hei.school.dto.request.UpdateSaleRequest;
import com.hei.school.dto.response.SaleResponse;
import com.hei.school.entity.*;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.SaleItemMapper;
import com.hei.school.mapper.SaleMapper;
import com.hei.school.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;
  private final SaleItemRepository saleItemRepository;
  private final CustomerRepository customerRepository;
  private final LibraryRepository libraryRepository;
  private final BookCopyRepository bookCopyRepository;
  private final SaleMapper saleMapper;
  private final SaleItemMapper saleItemMapper;

  @Transactional
  public SaleResponse create(CreateSaleRequest request) {
    Customer customer =
        customerRepository
            .findById(request.customerId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Customer with id " + request.customerId() + " not found"));
    Library library =
        libraryRepository
            .findById(request.libraryId())
            .orElseThrow(
                () ->
                    new NotFoundException("Library with id " + request.libraryId() + " not found"));

    Sale sale = saleMapper.toEntity(request, customer, library);

    List<SaleItem> saleItems =
        request.saleItems().stream()
            .map(
                itemRequest -> {
                  BookCopy bookCopy =
                      bookCopyRepository
                          .findById(itemRequest.bookCopyId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "BookCopy with id "
                                          + itemRequest.bookCopyId()
                                          + " not found"));
                  return saleItemMapper.toEntity(itemRequest, bookCopy, sale);
                })
            .toList();

    saleItems.forEach(sale.getSaleItems()::add);

    BigDecimal totalAmount =
        saleItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    sale.setTotalAmount(totalAmount);

    Sale finalSale = saleRepository.save(sale);

    return saleMapper.toResponse(finalSale);
  }

  public List<SaleResponse> getAll(UUID customerId, UUID libraryId, Instant from, Instant to) {
    if (customerId != null) {
      return saleRepository.findByCustomerId(customerId).stream()
          .map(saleMapper::toResponse)
          .toList();
    }
    if (libraryId != null) {
      return saleRepository.findByLibraryId(libraryId).stream()
          .map(saleMapper::toResponse)
          .toList();
    }
    if (from != null && to != null) {
      return saleRepository.findBySaleDateBetween(from, to).stream()
          .map(saleMapper::toResponse)
          .toList();
    }
    return saleRepository.findAll().stream().map(saleMapper::toResponse).toList();
  }

  public SaleResponse getById(UUID id) {
    Sale sale =
        saleRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sale with id " + id + " not found"));
    return saleMapper.toResponse(sale);
  }

  @Transactional
  public SaleResponse update(UUID id, UpdateSaleRequest request) {
    Sale sale =
        saleRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sale with id " + id + " not found"));

    saleItemRepository.deleteBySaleId(id);
    sale.getSaleItems().clear();

    List<SaleItem> saleItems =
        request.saleItems().stream()
            .map(
                itemRequest -> {
                  BookCopy bookCopy =
                      bookCopyRepository
                          .findById(itemRequest.bookCopyId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "BookCopy with id "
                                          + itemRequest.bookCopyId()
                                          + " not found"));
                  return saleItemMapper.toEntity(itemRequest, bookCopy, sale);
                })
            .toList();

    sale.getSaleItems().addAll(saleItems);

    saleItemRepository.saveAll(saleItems);

    BigDecimal totalAmount =
        saleItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    sale.setSaleDate(request.saleDate());
    sale.setTotalAmount(totalAmount);

    return saleMapper.toResponse(sale);
  }

  @Transactional
  public void delete(UUID id) {
    saleRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Sale with id " + id + " not found"));
    saleItemRepository.deleteBySaleId(id);
    saleRepository.deleteById(id);
  }
}
