package kr.co.ksgk.ims.domain.brand.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.member.entity.MemberBrand;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.*;

import java.nio.file.FileStore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    private String note;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberBrand> memberBrands = new ArrayList<>();

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public void update(String name, String note) {
        this.name = name;
        this.note = note;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
