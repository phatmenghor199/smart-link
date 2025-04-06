package com.menghor.smart_shop.feature.customer.models;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "banners")
@Data
@NoArgsConstructor
public class BannerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatusData status = StatusData.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;

    // Direct one-to-one relationship with image
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private ImageEntity image;
}