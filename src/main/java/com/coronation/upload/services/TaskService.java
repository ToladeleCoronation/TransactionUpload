package com.coronation.upload.services;

import com.coronation.upload.domain.*;

import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
public interface TaskService {
    List<Task> findByScheduleId(Long jobId);
    Task findByName(String name);
    Task findById(Long id);
    Task saveTask(Task task, DataConnection connection, Schedule schedule, DataTable table, Account account);
    List<Task> findAll();
    Task editTask(Task previous, Task task, DataConnection connection, Schedule schedule, DataTable table, Account account);
}
