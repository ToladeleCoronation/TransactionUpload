package com.coronation.upload.repo;

import com.coronation.upload.domain.DataColumn;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Toyin on 7/26/19.
 */
public interface ColumnRepository extends JpaRepository<DataColumn, Long> {
}
