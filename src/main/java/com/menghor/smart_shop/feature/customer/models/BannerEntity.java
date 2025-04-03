package com.menghor.smart_shop.feature.customer.models;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.enumations.StatusData;
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
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusData status = StatusData.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
}