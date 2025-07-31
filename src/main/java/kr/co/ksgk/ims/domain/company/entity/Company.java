package kr.co.ksgk.ims.domain.company.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.member.entity.MemberCompany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE company SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 20)
    private String businessNumber;

    @Column(length = 10)
    private String representativeName;

    private String address;

    @Lob
    private String note;

    private LocalDateTime deletedAt;

    @Builder
    public Company(String name, String businessNumber, String representativeName, String address, String note) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.representativeName = representativeName;
        this.address = address;
        this.note = note;
    }

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Brand> brands = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCompany> memberCompanies = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }

    public void updateBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public void updateRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateNote(String note) {
        this.note = note;
    }
}
