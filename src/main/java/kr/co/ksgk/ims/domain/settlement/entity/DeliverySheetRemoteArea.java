package kr.co.ksgk.ims.domain.settlement.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliverySheetRemoteArea extends BaseEntity {

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
    private String senderName;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverAddress;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer totalFee;

    public static DeliverySheetRemoteArea create(Integer year,
                                                 Integer month,
                                                 String pickupDate,
                                                 String invoiceNumber,
                                                 String senderName,
                                                 String receiverName,
                                                 String receiverAddress,
                                                 String productName,
                                                 Integer totalFee) {
        DeliverySheetRemoteArea row = new DeliverySheetRemoteArea();
        row.year = year;
        row.month = month;
        row.pickupDate = pickupDate;
        row.invoiceNumber = invoiceNumber;
        row.senderName = senderName;
        row.receiverName = receiverName;
        row.receiverAddress = receiverAddress;
        row.productName = productName;
        row.totalFee = totalFee;
        return row;
    }
}
