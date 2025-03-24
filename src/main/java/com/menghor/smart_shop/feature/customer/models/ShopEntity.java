package com.menghor.smart_shop.feature.customer.models;

import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "shops")
@Data
public class ShopEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<CategoryEntity> categories; // One shop has many categories

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<ProductEntity> products; // One shop has many products

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<BannerEntity> banners; // One shop can have many banners

}
