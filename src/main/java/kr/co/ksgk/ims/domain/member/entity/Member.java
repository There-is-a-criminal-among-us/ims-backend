package kr.co.ksgk.ims.domain.member.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String phone;

    @Lob
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime deletedAt;

    @Builder
    public Member(String username, String password, String name, String phone, String note, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.note = note;
        this.role = role;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberCompany> memberCompanies = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberBrand> memberBrands = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateNote(String note) {
        this.note = note;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateMemberCompanies(List<Company> managingCompanies) {
        this.memberCompanies.clear();
        managingCompanies.forEach(company -> {
            MemberCompany memberCompany = new MemberCompany(this, company);
            this.memberCompanies.add(memberCompany);
        });
    }

    public void updateMemberBrands(List<Brand> managingBrands) {
        this.memberBrands.clear();
        managingBrands.forEach(brand -> {
            MemberBrand memberBrand = new MemberBrand(this, brand);
            this.memberBrands.add(memberBrand);
        });
    }
}
