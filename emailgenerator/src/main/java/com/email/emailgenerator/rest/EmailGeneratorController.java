package com.email.emailgenerator.rest;

import com.email.emailgenerator.dto.EmailRequest;
import com.email.emailgenerator.exception.custom.ExternalApiException;
import com.email.emailgenerator.exception.custom.ResponseGenerationException;
import com.email.emailgenerator.service.EmailGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emailApi/email")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class EmailGeneratorController {
    
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generateResponse")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        log.info("Received EmailRequest: {}", emailRequest);
        log.info("Email Message Content: {}", emailRequest.getEmailMessageContent());

        try {
            String response = emailGeneratorService.generateEmailResponse(emailRequest);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException je) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while processing request: " + je.getMessage());
        } catch (ResponseGenerationException | ExternalApiException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating Response" + exception.getMessage());
        }
    }

}
