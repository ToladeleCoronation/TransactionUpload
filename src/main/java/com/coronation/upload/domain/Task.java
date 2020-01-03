package com.coronation.upload.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 7/25/19.
 */

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    private String fetchTable;

    private String fetchColumn;

    private String accountColumn;

    @NotNull
    private Boolean accountInData = Boolean.FALSE;

    @NotNull
    private Boolean amountInData = Boolean.FALSE;

    private String description;

    @NotNull
    @Email
    @Column(name = "senderEmail")
    private String senderEmail;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DataConnection connection;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DataTable table;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account account;

    private BigDecimal charge;

    @Column(nullable = false)
    private Boolean reconcile = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean bulkPayment = Boolean.TRUE;

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

    public String getFetchTable() {
        return fetchTable;
    }

    public void setFetchTable(String fetchTable) {
        this.fetchTable = fetchTable;
    }

    public String getFetchColumn() {
        return fetchColumn;
    }

    public void setFetchColumn(String fetchColumn) {
        this.fetchColumn = fetchColumn;
    }

    public String getAccountColumn() {
        return accountColumn;
    }

    public void setAccountColumn(String accountColumn) {
        this.accountColumn = accountColumn;
    }

    public Boolean getAccountInData() {
        return accountInData;
    }

    public void setAccountInData(Boolean accountInData) {
        this.accountInData = accountInData;
    }

    public Boolean getAmountInData() {
        return amountInData;
    }

    public void setAmountInData(Boolean amountInData) {
        this.amountInData = amountInData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataConnection getConnection() {
        return connection;
    }

    public void setConnection(DataConnection connection) {
        this.connection = connection;
    }

    public DataTable getTable() {
        return table;
    }

    public void setTable(DataTable table) {
        this.table = table;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Boolean getReconcile() {
        return reconcile;
    }

    public void setReconcile(Boolean reconcile) {
        this.reconcile = reconcile;
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

        Task task = (Task) o;

        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Boolean getBulkPayment() {
        return bulkPayment;
    }

    public void setBulkPayment(Boolean bulkPayment) {
        this.bulkPayment = bulkPayment;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
