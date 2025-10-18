package kr.co.ksgk.ims.domain.returns.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "return_handler_id", nullable = false)
    private ReturnHandler returnHandler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_mall_id", nullable = false)
    private ReturnMall returnMall;

    @Builder
    public ReturnInfo(String buyer, String receiver, String address, String phone, String productName, Integer quantity, String originalInvoice, String note, ReturnHandler returnHandler, ReturnMall returnMall) {
        this.buyer = buyer;
        this.receiver = receiver;
        this.address = address;
        this.phone = phone;
        this.productName = productName;
        this.quantity = quantity;
        this.originalInvoice = originalInvoice;
        this.returnStatus = ReturnStatus.REQUESTED;
        this.note = note;
        this.returnHandler = returnHandler;
        this.returnMall = returnMall;
    }

    public void patch(String buyer, String receiver, String address, String phone,
                      String productName, Integer quantity, String originalInvoice,
                      LocalDate acceptDate, ReturnStatus returnStatus, String returnInvoice,
                      String note, ReturnHandler returnHandler, ReturnMall returnMall) {
        if (buyer != null) this.buyer = buyer;
        if (receiver != null) this.receiver = receiver;
        if (address != null) this.address = address;
        if (phone != null) this.phone = phone;
        if (productName != null) this.productName = productName;
        if (quantity != null) this.quantity = quantity;
        if (originalInvoice != null) this.originalInvoice = originalInvoice;
        if (acceptDate != null) this.acceptDate = acceptDate;
        if (returnStatus != null) this.returnStatus = returnStatus;
        if (returnInvoice != null) this.returnInvoice = returnInvoice;
        if (note != null) this.note = note;
        if (returnHandler != null) this.returnHandler = returnHandler;
        if (returnMall != null) this.returnMall = returnMall;
    }

    public void accept() {
        this.acceptDate = LocalDate.now();
        this.returnStatus = ReturnStatus.IN_PROGRESS;
    }

    public void complete() {
        this.returnStatus = ReturnStatus.COMPLETED;
    }

    public void reRequest() {
        this.createdAt = LocalDateTime.now();
        this.returnStatus = ReturnStatus.REQUESTED;
    }
}
