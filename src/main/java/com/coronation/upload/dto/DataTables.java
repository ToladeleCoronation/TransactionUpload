package com.coronation.upload.dto;

import com.coronation.upload.domain.DataColumn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataTables {
    private Long id;

    private String name;

    private Boolean canReconcile = Boolean.FALSE;

    private Boolean amountInData = Boolean.FALSE;

    private String description;

    private List<DataColumn> columns = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime modifiedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DataColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataColumn> columns) {
        this.columns = columns;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getCanReconcile() {
        return canReconcile;
    }

    public void setCanReconcile(Boolean canReconcile) {
        this.canReconcile = canReconcile;
    }

    public Boolean getAmountInData() {
        return amountInData;
    }

    public void setAmountInData(Boolean amountInData) {
        this.amountInData = amountInData;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
