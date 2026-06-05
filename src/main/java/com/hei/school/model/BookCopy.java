package com.hei.school.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookCopy {

  public Integer copyId;
  public Integer bookId;
  public Integer libraryId;
  public BookFormat format;
  public BigDecimal copyPrice;
  public CopyStatus status;
  public LocalDate acquisitionDate;

  public BookCopy() {}

  public BookCopy(
      Integer copyId,
      Integer bookId,
      Integer libraryId,
      BookFormat format,
      BigDecimal copyPrice,
      CopyStatus status,
      LocalDate acquisitionDate) {
    this.copyId = copyId;
    this.bookId = bookId;
    this.libraryId = libraryId;
    this.format = format;
    this.copyPrice = copyPrice;
    this.status = status;
    this.acquisitionDate = acquisitionDate;
  }
}
