package com.menghor.smart_shop.feature.auth.repository;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @EntityGraph(attributePaths = {"shop", "roles"})
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.shop LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UserEntity> findUserWithShopById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"roles", "shop"})
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "shop"})
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.shop LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findWithRolesAndShopByUsername(@Param("username") String username);

//    Optional<UserEntity> findByUsername(String userna me);
//
//    @EntityGraph(attributePaths = {"roles", "shop"})
//    Optional<UserEntity> findWithRolesAndShopByUsername(String username);
//
//    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
//
//    Page<UserEntity> findByStatus(Status status, Pageable pageable);
//
//    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = :role")
//    Page<UserEntity> findByRole(@Param("role") RoleEnum role, Pageable pageable);
//
//    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE u.status = :status AND r.name = :role")
//    Page<UserEntity> findByStatusAndRole(@Param("status") Status status, @Param("role") RoleEnum role, Pageable pageable);
//
//    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE %:search% AND u.status = :status")
//    Page<UserEntity> findByUsernameContainingIgnoreCaseAndStatus(
//            @Param("search") String search,
//            @Param("status") Status status,
//            Pageable pageable);
//
//    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE u.username LIKE %:search% AND r.name = :role")
//    Page<UserEntity> findByUsernameContainingIgnoreCaseAndRole(
//            @Param("search") String search,
//            @Param("role") RoleEnum role,
//            Pageable pageable);
//
//    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE u.username LIKE %:search% AND u.status = :status AND r.name = :role")
//    Page<UserEntity> findByUsernameContainingIgnoreCaseAndStatusAndRole(
//            @Param("search") String search,
//            @Param("status") Status status,
//            @Param("role") RoleEnum role,
//            Pageable pageable);
//
//    Boolean existsByUsername(String username);
//
//    @EntityGraph(attributePaths = {"shop"})
//    Optional<UserEntity> findUserWithShopById(Long id);
//
//    @Query("SELECT DISTINCT u FROM UserEntity u " +
//            "LEFT JOIN FETCH u.roles r " +
//            "LEFT JOIN FETCH u.shop s")
//    List<UserEntity> findAllUsersWithRolesAndShop();
}