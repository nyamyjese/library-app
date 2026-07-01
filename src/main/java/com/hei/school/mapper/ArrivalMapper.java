package com.hei.school.mapper;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ArrivalMapper {

  public Arrival toEntity(CreateArrivalRequest request, BookCopy bookCopy) {
    Arrival arrival = new Arrival();
    arrival.setBookCopy(bookCopy);
    arrival.setQuantity(request.quantity());
    arrival.setUnitPrice(request.unitPrice());
    arrival.setArrivalDate(request.arrivalDate());
    return arrival;
  }

  public ArrivalResponse toResponse(Arrival arrival) {
    BigDecimal totalPrice =
        arrival.getUnitPrice().multiply(BigDecimal.valueOf(arrival.getQuantity()));

    return new ArrivalResponse(
        arrival.getId(),
        arrival.getBookCopy().getId(),
        arrival.getBookCopy().getBook().getTitle(),
        arrival.getBookCopy().getIsbn(),
        arrival.getQuantity(),
        arrival.getUnitPrice(),
        totalPrice,
        arrival.getArrivalDate());
  }
}
