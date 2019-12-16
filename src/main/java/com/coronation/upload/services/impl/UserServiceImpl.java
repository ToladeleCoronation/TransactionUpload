package com.coronation.upload.services.impl;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.User;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.dto.PasswordDto;
import com.coronation.upload.repo.UserRepository;
import com.coronation.upload.services.UserService;
import com.coronation.upload.util.GenericUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public void setUserRepository(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Page<User> listAll(BooleanExpression expression, Pageable pageable) {
		return userRepository.findAll(expression, pageable);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User save(User user) {
		user.setEmail(user.getEmail().toLowerCase());
		user.setPassword(passwordEncoder.encode(GenericUtil.generateRandomString(10)));
		return userRepository.saveAndFlush(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findByPhoneNumber(String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber);
	}


	@Override
	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User update(User prev, User current) {
		prev.setAddress(current.getAddress());
		prev.setFirstName(current.getFirstName());
		prev.setLastName(current.getLastName());
		prev.setPhoneNumber(current.getPhoneNumber());
		prev.setEmail(current.getEmail());
		prev.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(prev);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User disable(User prev) {
		if(prev.getDeleted())
		{
			prev.setStatus(GenericStatus.ACTIVE);
			prev.setDeleted(false);
		}
		else
		{
			prev.setStatus(GenericStatus.INACTIVE);
			prev.setDeleted(true);
		}

		prev.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(prev);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User delete(User user) {
		user.setDeleted(Boolean.TRUE);
		user.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(user);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User assignRole(User user, Role role) {
		user.setRole(role);
		return userRepository.saveAndFlush(user);
	}

	@Override
	public List<User> findByRoleName(String roleName) {
		return userRepository.findByRoleName(roleName);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User uploadImage(User user, byte[] image) {
		user.setProfileImage(image);
		user.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(user);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String resetPassword(User user) {
		String password = GenericUtil.generateRandomString(8);
		user.setPassword(passwordEncoder.encode(password));
		user.setModifiedAt(LocalDateTime.now());
		userRepository.saveAndFlush(user);
		return password;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public User changePassword(User user, PasswordDto passwordDto) {
		user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
		user.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(user);
	}

	@Override
	public List<User> findByParam(String param, Pageable pageable) {
		return userRepository.findByParam(param.toLowerCase(), pageable);
	}

	@Override
	public Long countAll() {
		return userRepository.count();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public String generatePassword(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			String password = GenericUtil.generateRandomString(8);
			user.setPassword(passwordEncoder.encode(password));
			userRepository.saveAndFlush(user);
			return password;
		}
		return null;
	}

}
