package com.coronation.upload.services.impl;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.enums.RoleType;
import com.coronation.upload.repo.RoleRepository;
import com.coronation.upload.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
	
	private RoleRepository roleRepository;
	
	@Autowired
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}


	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public Role findByName(RoleType roleType) {
		return roleRepository.findByName(roleType);
	}

	@Override
	public Role findById(Long id) {
		return roleRepository.findById(id).orElse(null);
	}
}
