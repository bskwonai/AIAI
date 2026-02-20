package com.example.aiai.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiGatewayService implements AiGatewayService {

    private final RestClient restClient;
    private final String embeddingModel;

    public OpenAiGatewayService(@Value("${ai.provider.base-url}") String baseUrl,
                                @Value("${ai.provider.api-key}") String apiKey,
                                @Value("${ai.provider.embedding-model}") String embeddingModel) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Double> embed(String text) {
        JsonNode node = restClient.post()
                .uri("/embeddings")
                .body(Map.of("model", embeddingModel, "input", text))
                .retrieve()
                .body(JsonNode.class);

        List<Double> result = new ArrayList<>();
        node.path("data").path(0).path("embedding").forEach(v -> result.add(v.asDouble()));
        return result;
    }

    @Override
    public String chat(String model, String systemPrompt, String userPrompt) {
        JsonNode node = restClient.post()
                .uri("/chat/completions")
                .body(Map.of(
                        "model", model,
                        "messages", List.of(
                                Map.of("role", "system", "content", systemPrompt),
                                Map.of("role", "user", "content", userPrompt)
                        )
                ))
                .retrieve()
                .body(JsonNode.class);

        return node.path("choices").path(0).path("message").path("content").asText();
    }
}
