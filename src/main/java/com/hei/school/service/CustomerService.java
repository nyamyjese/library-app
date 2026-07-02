package com.hei.school.service;

import com.hei.school.dto.request.CreateCustomerRequest;
import com.hei.school.dto.request.UpdateCustomerRequest;
import com.hei.school.dto.response.CustomerResponse;
import com.hei.school.entity.Customer;
import com.hei.school.exception.BadRequestException;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.CustomerMapper;
import com.hei.school.repository.CustomerRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  public CustomerResponse create(CreateCustomerRequest request) {
    if (customerRepository.existsByEmail(request.email())) {
      throw new BadRequestException("Email " + request.email() + " is already used");
    }
    if (customerRepository.existsByPhone(request.phone())) {
      throw new BadRequestException("Phone " + request.phone() + " is already used");
    }
    Customer customer = customerMapper.toEntity(request);
    Customer saved = customerRepository.save(customer);
    return customerMapper.toResponse(saved);
  }

  public List<CustomerResponse> getAll() {
    return customerRepository.findAll().stream().map(customerMapper::toResponse).toList();
  }

  public CustomerResponse getById(UUID id) {
    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
    return customerMapper.toResponse(customer);
  }

  public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
    if (!customer.getEmail().equals(request.email())
        && customerRepository.existsByEmail(request.email())) {
      throw new BadRequestException("Email " + request.email() + " is already used");
    }
    if (!customer.getPhone().equals(request.phone())
        && customerRepository.existsByPhone(request.phone())) {
      throw new BadRequestException("Phone " + request.phone() + " is already used");
    }
    customer.setFirstName(request.firstName());
    customer.setLastName(request.lastName());
    customer.setEmail(request.email());
    customer.setPhone(request.phone());
    Customer saved = customerRepository.save(customer);
    return customerMapper.toResponse(saved);
  }

  public void delete(UUID id) {
    customerRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
    customerRepository.deleteById(id);
  }
}
