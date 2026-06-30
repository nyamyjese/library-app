package com.hei.school.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(
    @NotBlank(message = "The first name is mandatory") String firstName,
    @NotBlank(message = "The last name is mandatory") String lastName,
    @NotBlank(message = "The email is mandatory") @Email(message = "The email is invalid")
        String email,
    @NotBlank(message = "The phone is mandatory") String phone) {}
