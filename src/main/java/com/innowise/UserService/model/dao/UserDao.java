package com.innowise.UserService.model.dao;

import com.innowise.UserService.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

import static com.innowise.UserService.query.UserQuery.*;

@Repository
public interface UserDao extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    Optional<User> findUserById(Long id);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = UPDATE_USER_BY_ID_NATIVE, nativeQuery = true)
    void updateUserById(@Param("user") User user);

    @Modifying
    @Query(value = ACTIVATE_USER_BY_ID_JPQL)
    int activateUserById(@Param("id") Long id);

    @Modifying
    @Query(value = DEACTIVATE_USER_BY_ID_JPQL)
    int deactivateUserById(@Param("id") Long id);
}
