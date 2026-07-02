package com.hei.school.repository;

import com.hei.school.entity.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  Optional<Customer> findByEmail(String email);
}
