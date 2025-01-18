package com.email.emailgenerator.service;

import com.email.emailgenerator.dto.EmailRequest;
import com.email.emailgenerator.exception.custom.ExternalApiException;
import com.email.emailgenerator.exception.custom.ResponseGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailGeneratorService {

    @Autowired
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String generateEmailResponse(EmailRequest emailRequest) throws JsonProcessingException {
        String prompt = buildPrompt(emailRequest);
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                }
        );

        String response = webClient
                .post()
                .uri(apiUrl + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new ExternalApiException("Error receiving response from external API.");
        }

        return getResponseContent(response);
    }

    private String getResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }  catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResponseGenerationException re) {
            throw new ResponseGenerationException(re.getMessage());
        }
    }

    protected String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        String tone = emailRequest.getEmailReplyTone();
        String message = emailRequest.getEmailMessageContent();
        prompt.append("Generate a professional email response for the following email message, do not generate an email subject. ");

        if (tone != null && !tone.isEmpty() && !tone.isBlank()) {
            prompt.append("Give your response in a ").append(tone).append(" tone.");
        }

        prompt.append("\nOriginal email: \n").append(message);
        log.info("Generated Prompt: {}", prompt.toString());
        return prompt.toString();
    }
}
