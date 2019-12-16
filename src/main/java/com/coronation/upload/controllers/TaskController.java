package com.coronation.upload.controllers;

import com.coronation.upload.domain.*;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Toyin on 8/1/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private TaskService taskService;
    private ScheduleService scheduleService;
    private TableService tableService;
    private AccountService accountService;
    private ConnectionService connectionService;
    private TransactionService transactionService;

    @Autowired
    public TaskController(TaskService taskService, ScheduleService scheduleService,
                          TableService tableService, AccountService accountService,
                          ConnectionService connectionService, TransactionService transactionService) {
        this.taskService = taskService;
        this.scheduleService = scheduleService;
        this.tableService = tableService;
        this.accountService = accountService;
        this.connectionService = connectionService;
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAnyRole('INITIALIZER')")
    @PostMapping("/schedules/{scheduleId}/connections/{connectionId}/tables/{tableId}/accounts/{accountId}")
    public ResponseEntity<Task> create(@RequestBody @Valid Task task, BindingResult bindingResult,
           @PathVariable("scheduleId") Long scheduleId, @PathVariable("connectionId") Long connectionId,
           @PathVariable("tableId") Long tableId, @PathVariable("accountId") Long accountId) {
        if (bindingResult.hasErrors() || (!task.getAmountInData() && (task.getCharge() == null ||
                task.getCharge().compareTo(BigDecimal.ZERO) <= 0))) {
            return ResponseEntity.badRequest().build();
        }

        if (taskService.findByName(task.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Schedule schedule = scheduleService.findById(scheduleId);
        DataTable table = tableService.findById(tableId);
        Account account = accountService.findById(accountId);
        DataConnection connection = null;
        try {
            connection = connectionService.findById(connectionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schedule == null || connection == null || table == null || account == null) {
            return ResponseEntity.notFound().build();
        }
        if (!account.getStatus().equals(GenericStatus.ACTIVE)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        if ((task.getReconcile() && !table.getCanReconcile()) || (task.getAmountInData() && !table.getAmountInData())
                || (!task.getAccountInData() && (task.getFetchTable() == null || task.getFetchTable().isEmpty() ||
                        task.getFetchColumn() == null || task.getFetchColumn().isEmpty() ||
                        task.getAccountColumn() == null || task.getAccountColumn().isEmpty()))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(taskService.saveTask(task, connection, schedule, table, account));
    }

    @PreAuthorize("hasAnyRole('INITIALIZER')")
    @PostMapping("/{id}/schedules/{scheduleId}/connections/{connectionId}/tables/{tableId}/accounts/{accountId}")
    public ResponseEntity<Task> edit(@PathVariable("id") Long id,
             @PathVariable("scheduleId") Long scheduleId, @PathVariable("connectionId") Long connectionId,
             @PathVariable("tableId") Long tableId, @PathVariable("accountId") Long accountId,
                         @RequestBody @Valid Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || (!task.getAmountInData() && (task.getCharge() == null ||
                task.getCharge().compareTo(BigDecimal.ZERO) <= 0))) {
            return ResponseEntity.badRequest().build();
        }

        Task previous = taskService.findById(id);
        Schedule schedule = scheduleService.findById(scheduleId);
        DataTable table = tableService.findById(tableId);
        Account account = accountService.findById(accountId);
        DataConnection connection = null;

        try {
            connection = connectionService.findById(connectionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schedule == null || connection == null || table == null || account == null || previous == null) {
            return ResponseEntity.notFound().build();
        }
        if (!account.getStatus().equals(GenericStatus.ACTIVE)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        if ((task.getReconcile() && !table.getCanReconcile()) || (task.getAmountInData() && !table.getAmountInData())
                 || (!task.getAccountInData() && (task.getFetchTable() == null || task.getFetchTable().isEmpty() ||
                        task.getFetchColumn() == null || task.getFetchColumn().isEmpty() ||
                        task.getAccountColumn() == null || task.getAccountColumn().isEmpty()))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(taskService.editTask(previous, task, connection, schedule, table, account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") Long id) {
        Task task = taskService.findById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PreAuthorize("hasAnyRole('INITIALIZER')")
    @PostMapping("/{id}/process")
    public ResponseEntity processTask(@PathVariable("id") Long id) throws SQLException {
        Task task = taskService.findById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        } else if (!task.getAccount().getStatus().equals(GenericStatus.ACTIVE)) {
            return ResponseEntity.unprocessableEntity().build();
        } else {
            transactionService.processTask(task);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<List<Task>> getByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        Schedule schedule = scheduleService.findById(scheduleId);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskService.findByScheduleId(scheduleId));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAll() {
        return ResponseEntity.ok(taskService.findAll());
    }
}