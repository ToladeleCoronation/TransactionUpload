package com.coronation.upload.repo;

import com.coronation.upload.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User> {
    User findByEmail(String email);

    User findByPhoneNumber(String phone);



    @Query("Select u from User u where lower(u.role.name) like CONCAT(:param, '%')" +
            " order by u.firstName")
    List<User> findByRoleName(@Param("param") String roleName);

    @Query("Select u from User u where lower(u.email) like CONCAT('%', :param, '%') " +
            "or lower(u.lastName) like CONCAT('%', :param, '%') " +
            "or lower(u.firstName) like CONCAT('%', :param, '%') " +
            "or lower(u.phoneNumber) like CONCAT('%', :param, '%') order by u.firstName")
    List<User> findByParam(@Param("param") String param, Pageable pageable);

}
