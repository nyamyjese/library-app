package com.hei.school.mapper;

import com.hei.school.dto.request.CreateSaleItemRequest;
import com.hei.school.dto.response.SaleItemResponse;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Sale;
import com.hei.school.entity.SaleItem;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class SaleItemMapper {

  public SaleItem toEntity(CreateSaleItemRequest request, BookCopy bookCopy, Sale sale) {
    SaleItem saleItem = new SaleItem();
    saleItem.setBookCopy(bookCopy);
    saleItem.setSale(sale);
    saleItem.setQuantity(request.quantity());
    saleItem.setUnitPrice(request.unitPrice());
    return saleItem;
  }

  public SaleItemResponse toResponse(SaleItem saleItem) {
    BigDecimal totalPrice =
        saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItem.getQuantity()));

    return new SaleItemResponse(
        saleItem.getId(),
        saleItem.getBookCopy().getId(),
        saleItem.getBookCopy().getBook().getTitle(),
        saleItem.getBookCopy().getIsbn(),
        saleItem.getQuantity(),
        saleItem.getUnitPrice(),
        totalPrice);
  }
}
