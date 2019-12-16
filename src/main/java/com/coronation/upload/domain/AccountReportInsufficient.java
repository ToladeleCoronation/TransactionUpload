package com.coronation.upload.domain;

public class AccountReportInsufficient {

    private String accountNumber;
    private String phoneNumber;
    private String amount;
    private String lienid;
    private Boolean leinRemoved;
    private String placedDate;
    private String response_message;
    private String transaction_date;
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLienid() {
        return lienid;
    }

    public void setLienid(String lienid) {
        this.lienid = lienid;
    }

    public Boolean getLeinRemoved() {
        return leinRemoved;
    }

    public void setLeinRemoved(Boolean leinRemoved) {
        this.leinRemoved = leinRemoved;
    }

    public String getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(String placedDate) {
        this.placedDate = placedDate;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

}
