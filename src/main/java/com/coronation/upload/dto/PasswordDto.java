package com.coronation.upload.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Toyin on 4/9/19.
 */
public class PasswordDto {
    @NotNull
    private String previousPassword;
    @NotNull
    private String newPassword;

    public String getPreviousPassword() {
        return previousPassword;
    }

    public void setPreviousPassword(String previousPassword) {
        this.previousPassword = previousPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
