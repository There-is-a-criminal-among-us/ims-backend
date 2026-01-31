package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliverySheetReturn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private String pickupDate;

    @Column(nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private String workType;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer amount;

    public static DeliverySheetReturn create(Integer year,
                                             Integer month,
                                             String pickupDate,
                                             String invoiceNumber,
                                             String workType,
                                             String senderName,
                                             String receiverName,
                                             String productName,
                                             Integer amount) {
        DeliverySheetReturn row = new DeliverySheetReturn();
        row.year = year;
        row.month = month;
        row.pickupDate = pickupDate;
        row.invoiceNumber = invoiceNumber;
        row.workType = workType;
        row.senderName = senderName;
        row.receiverName = receiverName;
        row.productName = productName;
        row.amount = amount;
        return row;
    }
}
