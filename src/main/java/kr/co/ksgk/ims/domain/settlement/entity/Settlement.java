package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"year", "month", "company_id"})
})
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.DRAFT;

    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private Member confirmedBy;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementDetail> details = new ArrayList<>();

    public static Settlement create(Integer year, Integer month, Company company) {
        Settlement settlement = new Settlement();
        settlement.year = year;
        settlement.month = month;
        settlement.company = company;
        settlement.status = SettlementStatus.DRAFT;
        return settlement;
    }

    public void confirm(Member confirmedBy) {
        this.status = SettlementStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.confirmedBy = confirmedBy;
    }

    public void revertToDraft() {
        this.status = SettlementStatus.DRAFT;
        this.confirmedAt = null;
        this.confirmedBy = null;
    }

    public void clearDetails() {
        this.details.clear();
    }

    public void addDetail(SettlementDetail detail) {
        detail.setSettlement(this);
        this.details.add(detail);
    }
}
