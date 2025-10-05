package kr.co.ksgk.ims.domain.returns.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReturnInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String buyer;

    @Column(length = 30, nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String address;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 30, nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 20, nullable = false)
    private String originalInvoice;

    private LocalDate acceptDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReturnStatus returnStatus;

    @Column(length = 20)
    private String returnInvoice;

    @Lob
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_registrar_id", nullable = false)
    private ReturnRegistrar returnRegistrar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_mall_id", nullable = false)
    private ReturnMall returnMall;
}
