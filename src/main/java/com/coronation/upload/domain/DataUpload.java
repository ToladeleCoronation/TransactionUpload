package com.coronation.upload.domain;

import com.coronation.upload.domain.enums.GenericStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 7/26/19.
 */

@Entity
@Table(name = "data_uploads")
public class DataUpload {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Task task;

    private Integer count;

    private Integer success;

    private Integer duplicate;

    private Integer unmatched;

    private Integer invalid;

    private Integer exceptions;

    private String successFile;
    private String duplicateFile;
    private String unmatchedFile;
    private String invalidFile;
    private String exceptionsFile;

    private String successfulDedbit;
    private String failedDebit;
    private String accountNotPresent;

    @NotNull
    private String uploadFile;

    private String reconcileFile;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GenericStatus status = GenericStatus.INACTIVE;

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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Integer duplicate) {
        this.duplicate = duplicate;
    }

    public Integer getUnmatched() {
        return unmatched;
    }

    public void setUnmatched(Integer unmatched) {
        this.unmatched = unmatched;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public Integer getExceptions() {
        return exceptions;
    }

    public void setExceptions(Integer exceptions) {
        this.exceptions = exceptions;
    }

    public GenericStatus getStatus() {
        return status;
    }

    public void setStatus(GenericStatus status) {
        this.status = status;
    }

    public String getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getReconcileFile() {
        return reconcileFile;
    }

    public void setReconcileFile(String reconcileFile) {
        this.reconcileFile = reconcileFile;
    }

    public String getSuccessFile() {
        return successFile;
    }

    public void setSuccessFile(String successFile) {
        this.successFile = successFile;
    }

    public String getDuplicateFile() {
        return duplicateFile;
    }

    public void setDuplicateFile(String duplicateFile) {
        this.duplicateFile = duplicateFile;
    }

    public String getUnmatchedFile() {
        return unmatchedFile;
    }

    public void setUnmatchedFile(String unmatchedFile) {
        this.unmatchedFile = unmatchedFile;
    }

    public String getInvalidFile() {
        return invalidFile;
    }

    public void setInvalidFile(String invalidFile) {
        this.invalidFile = invalidFile;
    }

    public String getExceptionsFile() {
        return exceptionsFile;
    }

    public void setExceptionsFile(String exceptionsFile) {
        this.exceptionsFile = exceptionsFile;
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

    public String getSuccessfulDedbit() {
        return successfulDedbit;
    }

    public void setSuccessfulDedbit(String successfulDedbit) {
        this.successfulDedbit = successfulDedbit;
    }

    public String getFailedDebit() {
        return failedDebit;
    }

    public void setFailedDebit(String failedDebit) {
        this.failedDebit = failedDebit;
    }

    public String getAccountNotPresent() {
        return accountNotPresent;
    }

    public void setAccountNotPresent(String accountNotPresent) {
        this.accountNotPresent = accountNotPresent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataUpload that = (DataUpload) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
