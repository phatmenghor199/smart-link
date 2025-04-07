package com.menghor.smart_shop.feature.setting.model;

import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
public class ImageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String imageType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String base64Image;

    // Reference field to track what entity type owns this image (e.g., "category", "banner", "product")
    // Null means the image is not associated with any entity yet
    private String referenceType;

    // Add a transient method to get URL instead of direct base64 data
    @Transient
    public String getImageUrl() {
        if (id == null) return null;
        return "/api/v1/images/" + id;
    }
}