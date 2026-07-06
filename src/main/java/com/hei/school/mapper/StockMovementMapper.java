package com.hei.school.mapper;

import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.SaleItem;
import com.hei.school.entity.StockMovement;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovement toEntity(
            Integer quantity,
            MovementType movementType,
            MovementReason reason,
            BookCopy bookCopy,
            Arrival arrival,
            SaleItem saleItem,
            Instant movementDate) {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setQuantity(quantity);
        stockMovement.setMovementType(movementType);
        stockMovement.setReason(reason);
        stockMovement.setBookCopy(bookCopy);
        stockMovement.setArrival(arrival);
        stockMovement.setSaleItem(saleItem);
        stockMovement.setMovementDate(movementDate);
        return stockMovement;
    }

    public StockMovementResponse toResponse(StockMovement sm) {
        return new StockMovementResponse(
                sm.getId(),
                sm.getQuantity(),
                sm.getMovementType(),
                sm.getReason(),
                sm.getBookCopy().getId(),
                sm.getBookCopy().getBook().getTitle(),
                sm.getBookCopy().getIsbn(),
                sm.getArrival() != null ? sm.getArrival().getId() : null,
                sm.getSaleItem() != null ? sm.getSaleItem().getId() : null,
                sm.getMovementDate());
    }
}