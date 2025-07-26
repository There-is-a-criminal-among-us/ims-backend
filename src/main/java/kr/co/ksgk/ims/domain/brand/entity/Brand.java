package kr.co.ksgk.ims.domain.brand.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.company.entity.Company;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String name;

    @Lob
    private String note;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberBrand> memberBrands = new ArrayList<>();

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }
    public void updateNote(String note) {
        this.note = note;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

}
