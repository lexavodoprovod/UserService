package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

import static com.innowise.userservice.query.UserQuery.*;

@Repository
public interface UserDao extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    Optional<User> findUserById(Long id);

    boolean existsByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"paymentCards"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @Modifying
    @Query(value = UPDATE_USER_BY_ID_NATIVE, nativeQuery = true)
    void updateUserById(@Param("user") User user);

    @Modifying
    @Query(value = ACTIVATE_USER_BY_ID_JPQL)
    int activateUserById(@Param("id") Long id);

    @Modifying
    @Query(value = DEACTIVATE_USER_BY_ID_JPQL)
    int deactivateUserById(@Param("id") Long id);

    int deleteUserById(Long id);
}
