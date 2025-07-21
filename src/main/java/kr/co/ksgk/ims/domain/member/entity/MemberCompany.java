package kr.co.ksgk.ims.domain.member.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.company.entity.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MemberCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public MemberCompany(Member member, Company company)
    {
        this.member = member;
        this.company = company;
    }
}
