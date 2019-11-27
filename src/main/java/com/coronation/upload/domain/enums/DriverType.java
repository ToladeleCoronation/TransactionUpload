package com.coronation.upload.domain.enums;

/**
 * Created by Toyin on 7/25/19.
 */
public enum DriverType {
    MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver"), MYSQL("com.mysql.cj.jdbc.Driver"),
        ORACLE(""), POSTGRES("org.postgresql.Driver");

    private String value;

    DriverType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
