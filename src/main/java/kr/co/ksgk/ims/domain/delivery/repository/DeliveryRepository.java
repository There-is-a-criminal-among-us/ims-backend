package kr.co.ksgk.ims.domain.delivery.repository;

import kr.co.ksgk.ims.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryCustomRepository {
}
