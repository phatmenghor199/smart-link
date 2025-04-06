package com.menghor.smart_shop.feature.setting.repository;

import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {

    // Find images that don't have a reference type AND are older than the specified time
    @Query("SELECT i FROM ImageEntity i WHERE i.referenceType IS NULL AND i.createdAt < :timeLimit")
    List<ImageEntity> findUnreferencedImagesCreatedBefore(@Param("timeLimit") LocalDateTime timeLimit);

    // Find all images by reference type (for statistics or management)
    List<ImageEntity> findByReferenceType(String referenceType);

}