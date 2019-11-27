package com.coronation.upload.dto;

/**
 * Created by Toyin on 6/3/19.
 */
public class ADLoginResponse {
    private boolean status;
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
