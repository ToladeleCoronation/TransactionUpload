package com.coronation.upload.repo;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleType roleName);
}
