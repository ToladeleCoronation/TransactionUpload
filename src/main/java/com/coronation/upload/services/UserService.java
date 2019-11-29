package com.coronation.upload.services;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.User;
import com.coronation.upload.dto.PasswordDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
	Page<User> listAll(BooleanExpression expression, Pageable pageable);
    User save(User user);
    User findByEmail(String email);
	User findByPhoneNumber(String phoneNumber);
	User findById(Long userId);
    User update(User prev, User current);
    User delete(User user);
	User assignRole(User user, Role role);
	List<User> findByRoleName(String roleName);
	User uploadImage(User user, byte[] image);
	String resetPassword(User user);
	User changePassword(User user, PasswordDto passwordDto);
	List<User> findByParam(String param, Pageable pageable);
	Long countAll();
	String generatePassword(String email);

}
