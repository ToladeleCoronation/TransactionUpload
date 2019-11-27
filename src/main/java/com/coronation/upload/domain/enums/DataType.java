package com.coronation.upload.domain.enums;

/**
 * Created by Toyin on 7/25/19.
 */
public enum DataType {
    BIG_DECIMAL("decimal(19,2)"), LONG("bigint(20)"), INTEGER("int(11)"), LOCAL_DATETIME("datetime(6)"),
            LOCAL_DATE("date"), VARCHAR("varchar(255)"), DOUBLE("double"), BOOLEAN("bit(1)");

    private String value;

    DataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}