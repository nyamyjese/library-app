package com.hei.school.mapper;

import com.hei.school.dto.request.CreateCustomerRequest;
import com.hei.school.dto.response.CustomerResponse;
import com.hei.school.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

  public Customer toEntity(CreateCustomerRequest request) {
    Customer customer = new Customer();
    customer.setFirstName(request.firstName());
    customer.setLastName(request.lastName());
    customer.setEmail(request.email());
    customer.setPhone(request.phone());
    return customer;
  }

  public CustomerResponse toResponse(Customer customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getFirstName(),
        customer.getLastName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getCreatedAt(),
        customer.getUpdatedAt());
  }
}
