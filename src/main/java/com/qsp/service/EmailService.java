package com.qsp.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;



@Service
public class EmailService {

    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EmailService(
            @Value("${brevo.api.key}") String apiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name}") String senderName,
            ObjectMapper objectMapper) {

        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendEmail(String to, String subject, String body) {

        try {

            Map<String, Object> payload = Map.of(
                    "sender", Map.of(
                            "name", senderName,
                            "email", senderEmail
                    ),
                    "to", List.of(
                            Map.of("email", to)
                    ),
                    "subject", subject,
                    "htmlContent", body
            );

            String jsonBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

                throw new RuntimeException(
                        "Brevo email failed. Status: "
                        + response.statusCode()
                        + ", Response: "
                        + response.body()
                );
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Email sending was interrupted", e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send email: " + e.getMessage(), e
            );
        }
    }
}