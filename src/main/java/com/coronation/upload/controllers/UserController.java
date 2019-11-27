package com.coronation.upload.controllers;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.User;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.domain.enums.RoleType;
import com.coronation.upload.repo.predicate.CustomPredicateBuilder;
import com.coronation.upload.repo.predicate.Operation;
import com.coronation.upload.security.ProfileDetails;
import com.coronation.upload.services.RoleService;
import com.coronation.upload.services.UserService;
import com.coronation.upload.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;
    private RoleService roleService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, RoleService roleService,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyRole('IT_ADMIN')")
    @PostMapping("/roles/{roleName}")
    public ResponseEntity<User> create(@PathVariable("roleName") RoleType roleName,
                                       @RequestBody @Valid User user, BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Role role = roleService.findByName(roleName);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        user.setRole(role);
        user.setStatus(GenericStatus.ACTIVE);
        try {
            user = userService.save(user);
            return ResponseEntity.ok(user);
        } catch (DataIntegrityViolationException dve) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PreAuthorize("hasAnyRole('IT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> edit(@PathVariable("id") Long id,
        @RequestBody @Valid User newData, BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = userService.findById(id);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            user = userService.update(user, newData);
            return ResponseEntity.ok(user);
        } catch (DataIntegrityViolationException dve) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PreAuthorize("hasAnyRole('IT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.delete(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('IT_ADMIN')")
    @PutMapping("/{id}/roles/{roleId}")
    public ResponseEntity<User> assignRole(@PathVariable("id") Long id, @PathVariable("roleId") Long roleId) {
        User user = userService.findById(id);
        Role role = roleService.findById(roleId);
        if (user == null || role == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.assignRole(user, role));
    }

    @GetMapping
    public ResponseEntity<Page<User>> listUser(@RequestParam(value="page", required = false, defaultValue = "0") int page,
                                               @RequestParam(value="pageSize", defaultValue = "10") int pageSize, @RequestParam(value="firstname", required = false) String firstname,
                                               @RequestParam(value="lastname", required = false) String lastname, @RequestParam(value="email", required = false) String email,
                                               @RequestParam(value="phoneNumber", required = false) String phoneNumber,
                                               @RequestParam(value="role", required = false) RoleType role) {
        BooleanExpression filter = new CustomPredicateBuilder<>("user", User.class)
                .with("firstName", Operation.LIKE, firstname)
                .with("lastName", Operation.LIKE, lastname)
                .with("email", Operation.LIKE, email)
                .with("phoneNumber", Operation.LIKE, phoneNumber)
                .with("role.name", Operation.ENUM, role).build();
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.asc("firstName"), Sort.Order.asc("lastName")));
        return ResponseEntity.ok(userService.listAll(filter, pageRequest));
    }

    @GetMapping(value = "/params")
    public ResponseEntity<List<User>> findByParams(@RequestParam(value = "q", required = false) String q){
        if (q == null) {
            q = "";
        }
        return ResponseEntity.ok(userService.findByParam(q, PageRequest.of(0, 10)));
    }

    @GetMapping(value = "/me")
    public ResponseEntity<User> getUserInSession(@AuthenticationPrincipal ProfileDetails profileDetails){
        return ResponseEntity.ok(profileDetails.toUser());
    }

    @GetMapping(value = "/roles/{roleName}")
    public ResponseEntity<List<User>> getByRole(@PathVariable("roleName") String roleName) {
        return ResponseEntity.ok(userService.findByRoleName(roleName));
    }
}
