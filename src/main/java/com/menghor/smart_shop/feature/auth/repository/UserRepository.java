package com.menghor.smart_shop.feature.auth.repository;

import com.menghor.smart_shop.feature.auth.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"shop"})  // Specify which relations to fetch eagerly
    Optional<UserEntity> findUserWithShopById(Long id);
}