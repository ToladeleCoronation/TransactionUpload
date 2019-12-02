package com.coronation.upload.domain;

import java.util.List;

public class AccountCount {

    private String number;
    private List<Long> id;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }
}
