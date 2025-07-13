package kr.co.ksgk.ims.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RawDeliveryItem {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rawName;

    @OneToMany(mappedBy = "rawDeliveryItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryItemMapping> deliveryItemMappings = new ArrayList<>();
}
