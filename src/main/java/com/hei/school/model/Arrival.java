package com.hei.school.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Arrival {

  public Integer arrivalId;
  public Integer bookId;
  public Integer libraryId;
  public BookFormat format;
  public Integer quantity;
  public LocalDate arrivalDate;
  public BigDecimal unitCost;

  public Arrival() {}

  public Arrival(
      Integer arrivalId,
      Integer bookId,
      Integer libraryId,
      BookFormat format,
      Integer quantity,
      LocalDate arrivalDate,
      BigDecimal unitCost) {
    this.arrivalId = arrivalId;
    this.bookId = bookId;
    this.libraryId = libraryId;
    this.format = format;
    this.quantity = quantity;
    this.arrivalDate = arrivalDate;
    this.unitCost = unitCost;
  }
}
