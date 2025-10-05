package kr.co.ksgk.ims.domain.returns.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReturnHandler extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Builder
    public ReturnHandler(String name, Brand brand) {
        this.name = name;
        this.brand = brand;
    }

    @OneToMany(mappedBy = "returnHandler", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnInfo> returnInfos = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }
}
