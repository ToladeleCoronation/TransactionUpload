package com.coronation.upload.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toyin on 7/25/19.
 */

@Entity
@Table(name = "data_tables")
public class DataTable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp="^[A-Za-z].*$")
    @NotNull
    @Column(unique = true)
    private String name;

    @Pattern(regexp="^[A-Za-z].*$")
    @Column(unique = true)
    private String insufficientBalance;


    @Pattern(regexp="^[A-Za-z].*$")
    @Column(unique = true)
    private String phoneAccountHolder;

    @NotNull
    private Boolean canReconcile = Boolean.FALSE;

    @NotNull
    private Boolean amountInData = Boolean.FALSE;

    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_table_id")
    private List<DataColumn> columns = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataTable dataTable = (DataTable) o;

        return id != null ? id.equals(dataTable.id) : dataTable.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getInsufficientBalance() {
        return insufficientBalance;
    }

    public void setInsufficientBalance(String insufficientBalance) {
        this.insufficientBalance = insufficientBalance;
    }

    public String getPhoneAccountHolder() {
        return phoneAccountHolder;
    }

    public void setPhoneAccountHolder(String phoneAccountHolder) {
        this.phoneAccountHolder = phoneAccountHolder;
    }
}
