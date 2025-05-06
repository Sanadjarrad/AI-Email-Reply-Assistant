package com.email.emailgenerator.service;
import com.email.emailgenerator.dto.EmailRequest;
import com.email.emailgenerator.exception.custom.ExternalApiException;
import com.email.emailgenerator.exception.custom.ResponseGenerationException;
import com.email.emailgenerator.rest.EmailGeneratorController;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
public class EmailGeneratorServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailGeneratorService emailGeneratorService;

    @MockitoBean
    private EmailGeneratorController emailGeneratorController;
    private EmailRequest emailRequest;

    @BeforeEach
    void setup() {
        emailRequest = new EmailRequest(
                "Why is Spring Boot better than NodeJs?", "professional"
        );
    }

    @Test
    void verifyPromptBuilding() {
        String prompt = emailGeneratorService.buildPrompt(emailRequest);

        System.out.println(prompt);
        assertNotNull(prompt);
        assertTrue(prompt.contains("Why is Spring Boot better than NodeJs?"));
        assertTrue(prompt.contains("Give your response in a professional tone."));
    }

    @Test
    void verifyEmailResponseGeneration() throws JsonProcessingException {
        String response = emailGeneratorService.generateEmailResponse(emailRequest);

        System.out.println(response);
        assertNotNull(response);
        assertTrue(response.contains("Spring Boot"));
    }

    @Test
    void verifyEmailResponse() throws Exception {
        String mockResponse = "{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"Test Response\"}]}}]}";
        when(emailGeneratorController.generateEmail(any(EmailRequest.class))).thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(post("/emailApi/email/generateResponse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailMessageContent\": \"Why is Spring Boot better than NodeJs?\", \"emailReplyTone\": \"professional\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidates[0].content.parts[0].text").value("Test Response"));
    }

    // Should not raise exceptions as the prompt can be provided without the tone
    @Test
    void invalidTone() {
        emailRequest = new EmailRequest(
                "Some email message", "invalid-tone"
        );

        assertDoesNotThrow(() -> {
            emailGeneratorService.generateEmailResponse(emailRequest);
        });

        System.out.println("No errors were thrown for invalid tone.");
    }

}
