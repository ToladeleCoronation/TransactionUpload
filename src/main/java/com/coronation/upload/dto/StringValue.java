package com.coronation.upload.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Toyin on 4/10/19.
 */
public class StringValue {
    @NotNull
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
