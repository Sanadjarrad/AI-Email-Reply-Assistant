package com.email.emailgenerator.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

    private String emailMessageContent;
    private String emailReplyTone;

}
