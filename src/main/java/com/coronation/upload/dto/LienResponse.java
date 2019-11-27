package com.coronation.upload.dto;

public class LienResponse {

    private String appNumber;
    private String mode;
    private String accountId;
    private String msgDateTime;
    private String status;
    private String responseDescription;

    public String getAppNumber() {
        return appNumber;
    }

    public void setAppNumber(String appNumber) {
        this.appNumber = appNumber;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMsgDateTime() {
        return msgDateTime;
    }

    public void setMsgDateTime(String msgDateTime) {
        this.msgDateTime = msgDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }
}
