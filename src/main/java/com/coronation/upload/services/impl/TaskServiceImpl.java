package com.coronation.upload.services.impl;

import com.coronation.upload.domain.*;
import com.coronation.upload.repo.TaskRepository;
import com.coronation.upload.services.TaskService;
import com.coronation.upload.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;
    private Utilities utilities;
    @Value("${app.secret.key}")
    private String appKey;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> findByScheduleId(Long jobId) {
        return taskRepository.findByScheduleId(jobId);
    }

    @Override
    public Task findByName(String name) {
        return taskRepository.findByNameEquals(name);
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task saveTask(Task task, DataConnection connection, Schedule schedule, DataTable table, Account account) {
        connection.setPassword(utilities.encrypt(connection.getPassword(), appKey));
        task.setConnection(connection);
        task.setSchedule(schedule);
        task.setTable(table);
        task.setAccount(account);
        return taskRepository.saveAndFlush(task);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task editTask(Task previous, Task task, DataConnection connection, Schedule schedule,
                         DataTable table, Account account) {
        connection.setPassword(utilities.encrypt(connection.getPassword(), appKey));
        previous.setConnection(connection);
        previous.setSchedule(schedule);
        previous.setTable(table);
        previous.setAccount(account);
        previous.setModifiedAt(LocalDateTime.now());
        previous.setCharge(task.getCharge());
        previous.setFetchTable(task.getFetchTable());
        previous.setFetchColumn(task.getFetchColumn());
        previous.setAccountColumn(task.getAccountColumn());
        previous.setName(task.getName());
        previous.setReconcile(task.getReconcile());
        previous.setDescription(task.getDescription());
        previous.setAmountInData(task.getAmountInData());
        previous.setBulkPayment(task.getBulkPayment());
        previous.setAccountInData(task.getAccountInData());
        previous.setModifiedAt(LocalDateTime.now());
        return taskRepository.saveAndFlush(previous);
    }
}
