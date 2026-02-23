package com.example.aiai.service;

import java.util.List;

public interface AiGatewayService {
    List<Double> embed(String text);
    String chat(String model, String systemPrompt, String userPrompt);
}
