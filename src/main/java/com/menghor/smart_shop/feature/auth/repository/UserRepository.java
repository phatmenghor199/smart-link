package com.menghor.smart_shop.feature.auth.repository;

import com.menghor.smart_shop.feature.auth.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "shop"})
    Optional<UserEntity> findWithRolesAndShopByUsername(String username);

    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"shop"})
    Optional<UserEntity> findUserWithShopById(Long id);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH u.shop s")
    List<UserEntity> findAllUsersWithRolesAndShop();
}