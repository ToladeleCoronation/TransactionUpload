package com.coronation.upload.repo;

import com.coronation.upload.domain.DataUpload;
import com.coronation.upload.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by Toyin on 7/30/19.
 */
public interface UploadRepository extends JpaRepository<DataUpload, Long>, QuerydslPredicateExecutor<DataUpload> {
    DataUpload findByTask(Task task);
}
