package com.coronation.upload.domain;

import com.coronation.upload.domain.enums.DataType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 7/25/19.
 */

@Entity
@Table(name = "data_columns")
public class DataColumn {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp="^[A-Za-z].*$")
    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DataType dataType;

    @Column
    private Integer dataSize;

    @Column(nullable = false)
    private Boolean identifier = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean narration = Boolean.FALSE;

    @Column(name = "unique_column", nullable = false)
    private Boolean unique = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean reconciliation = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean amountField = Boolean.FALSE;

    @Column(name = "column_order", nullable = false)
    private Integer order;

    @Transient
    private String value;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="modified_at")
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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Integer getDataSize() {
        return dataSize;
    }

    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    public Boolean getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Boolean identifier) {
        this.identifier = identifier;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getReconciliation() {
        return reconciliation;
    }

    public void setReconciliation(Boolean reconciliation) {
        this.reconciliation = reconciliation;
    }

    public Boolean getAmountField() {
        return amountField;
    }

    public void setAmountField(Boolean amountField) {
        this.amountField = amountField;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataColumn column = (DataColumn) o;

        return id != null ? id.equals(column.id) : column.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Boolean getNarration() {
        return narration;
    }

    public void setNarration(Boolean narration) {
        this.narration = narration;
    }
}
