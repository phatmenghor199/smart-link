package com.menghor.smart_shop.feature.setting.model;

import com.menghor.smart_shop.utils.database.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    @Column(columnDefinition = "TEXT")
    private String base64Image;
}