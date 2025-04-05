package com.menghor.smart_shop.feature.customer.models;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class CategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Enumerated(EnumType.STRING)
    private StatusData status = StatusData.ACTIVE;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop; // Each category belongs to one shop

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductEntity> products; // One category can have many products
}