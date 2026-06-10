package kr.co.ksgk.ims.domain.payslip.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import kr.co.ksgk.ims.domain.member.entity.Member;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "date"}))
public class Payslip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 7)
    private String date;

    @Column(nullable = false)
    private String s3Key;

    @Builder
    public Payslip(Member member, String date, String s3Key) {
        this.member = member;
        this.date = date;
        this.s3Key = s3Key;
    }

    public void update(Member member, String s3Key) {
        this.member = member;
        this.s3Key = s3Key;
    }
}
