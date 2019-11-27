package com.coronation.upload.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Toyin on 6/6/19.
 */
public class MailRequest {
    @NotNull
    private String subject;
    @NotNull
    private String message;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
