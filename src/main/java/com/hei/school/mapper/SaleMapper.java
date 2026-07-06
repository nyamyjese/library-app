package com.hei.school.mapper;

import com.hei.school.dto.request.CreateSaleRequest;
import com.hei.school.dto.response.SaleItemResponse;
import com.hei.school.dto.response.SaleResponse;
import com.hei.school.entity.Customer;
import com.hei.school.entity.Library;
import com.hei.school.entity.Sale;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleMapper {

  private final SaleItemMapper saleItemMapper;

  public Sale toEntity(CreateSaleRequest request, Customer customer, Library library) {
    Sale sale = new Sale();
    sale.setCustomer(customer);
    sale.setLibrary(library);
    sale.setSaleDate(request.saleDate());
    sale.setTotalAmount(java.math.BigDecimal.ZERO);
    sale.setSaleItems(new ArrayList<>());
    return sale;
  }

  public SaleResponse toResponse(Sale sale) {
    List<SaleItemResponse> saleItems =
        sale.getSaleItems() == null
            ? new ArrayList<>()
            : sale.getSaleItems().stream().map(saleItemMapper::toResponse).toList();

    return new SaleResponse(
        sale.getId(),
        sale.getCustomer().getId(),
        sale.getCustomer().getFirstName(),
        sale.getCustomer().getLastName(),
        sale.getLibrary().getId(),
        sale.getLibrary().getName(),
        sale.getSaleDate(),
        sale.getTotalAmount(),
        saleItems,
        sale.getCreatedAt(),
        sale.getUpdatedAt());
  }
}
