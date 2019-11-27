package com.coronation.upload.services;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.enums.RoleType;

import java.util.List;

public interface RoleService {
	List<Role> findAll();
	Role findByName(RoleType roleType);
	Role findById(Long id);
}
