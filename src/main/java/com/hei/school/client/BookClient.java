package com.hei.school.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;


@Component
public class BookClient {

    private final RestClient restClient;

    public BookClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:8080/api/").build();
    }

    public boolean bookExists(UUID bookId) {
        try {
            restClient.get()
                    .uri("/books/{id}", bookId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}