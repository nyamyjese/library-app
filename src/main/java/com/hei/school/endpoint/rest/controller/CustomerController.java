package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.CreateCustomerRequest;
import com.hei.school.dto.request.UpdateCustomerRequest;
import com.hei.school.dto.response.CustomerResponse;
import com.hei.school.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse create(@RequestBody @Valid CreateCustomerRequest request) {
    return customerService.create(request);
  }

  @GetMapping
  public List<CustomerResponse> getAll() {
    return customerService.getAll();
  }

  @GetMapping("/{id}")
  public CustomerResponse getById(@PathVariable UUID id) {
    return customerService.getById(id);
  }

  @PutMapping("/{id}")
  public CustomerResponse update(
      @PathVariable UUID id, @RequestBody @Valid UpdateCustomerRequest request) {
    return customerService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    customerService.delete(id);
  }
}
