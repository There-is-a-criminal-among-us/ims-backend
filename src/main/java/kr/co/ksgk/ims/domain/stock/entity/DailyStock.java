package kr.co.ksgk.ims.domain.stock.entity;

import jakarta.persistence.*;
import kr.co.ksgk.ims.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DailyStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer inboundTotal;

    @Column(nullable = false)
    private Integer outboundTotal;

    @Column(nullable = false)
    private Integer adjustmentTotal;

    @Column(nullable = false)
    LocalDate stockDate;

    @OneToMany(mappedBy = "dailyStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
}
