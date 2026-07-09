package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.request.CreateCustomerRequest;
import com.hei.school.dto.response.CustomerResponse;
import com.hei.school.entity.Customer;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  private final CustomerMapper mapper = new CustomerMapper();

  @Test
  void toEntity_shouldMapAllFields() {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    Customer customer = mapper.toEntity(request);

    assertThat(customer.getFirstName()).isEqualTo("Rakoto");
    assertThat(customer.getLastName()).isEqualTo("Andriantsoa");
    assertThat(customer.getEmail()).isEqualTo("rakoto@gmail.com");
    assertThat(customer.getPhone()).isEqualTo("0341112233");
  }

  @Test
  void toResponse_shouldMapAllFields() {
    UUID customerId = UUID.randomUUID();
    Instant now = Instant.now();

    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setFirstName("Rakoto");
    customer.setLastName("Andriantsoa");
    customer.setEmail("rakoto@gmail.com");
    customer.setPhone("0341112233");
    customer.setCreatedAt(now);
    customer.setUpdatedAt(now);

    CustomerResponse response = mapper.toResponse(customer);

    assertThat(response.id()).isEqualTo(customerId);
    assertThat(response.firstName()).isEqualTo("Rakoto");
    assertThat(response.lastName()).isEqualTo("Andriantsoa");
    assertThat(response.email()).isEqualTo("rakoto@gmail.com");
    assertThat(response.phone()).isEqualTo("0341112233");
    assertThat(response.createdAt()).isEqualTo(now);
    assertThat(response.updatedAt()).isEqualTo(now);
  }
}
