package com.coronation.upload.repo;

import com.coronation.upload.domain.DataTable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Toyin on 7/26/19.
 */
public interface TableRepository extends JpaRepository<DataTable, Long> {
    DataTable findByNameEquals(String name);
}
