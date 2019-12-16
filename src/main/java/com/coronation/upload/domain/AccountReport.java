package com.coronation.upload.domain;

public class AccountReport {
    private Long id;
    private String phoneNumber;
    private String accountNumber;
    private String count;
    private String amount;
    private String status;
    private String transactionDate;
    private Boolean statusOfresponseMessage;
    private Long insufficient;
    private String createdAt;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Boolean getStatusOfresponseMessage() {
        return statusOfresponseMessage;
    }

    public void setStatusOfresponseMessage(Boolean statusOfresponseMessage) {
        this.statusOfresponseMessage = statusOfresponseMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInsufficient() {
        return insufficient;
    }

    public void setInsufficient(Long insufficient) {
        this.insufficient = insufficient;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
