package com.hei.school.repository;

import com.hei.school.entity.Arrival;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, UUID> {

  List<Arrival> findAllByBookCopy_Id(UUID bookCopyId);

  List<Arrival> findAllByBookCopy_Book_Id(UUID bookId);
}
