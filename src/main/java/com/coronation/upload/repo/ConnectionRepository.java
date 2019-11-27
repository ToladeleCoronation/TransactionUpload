package com.coronation.upload.repo;

import com.coronation.upload.domain.DataConnection;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Toyin on 7/26/19.
 */
public interface ConnectionRepository extends JpaRepository<DataConnection, Long> {
    DataConnection findByNameEquals(String name);
}
