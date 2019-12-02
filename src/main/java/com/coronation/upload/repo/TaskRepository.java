package com.coronation.upload.repo;

import com.coronation.upload.domain.DataTable;
import com.coronation.upload.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 7/26/19.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByScheduleId(Long scheduleId);
    Task findByNameEquals(String name);
   Optional <Task> findByTable(DataTable table);
}
