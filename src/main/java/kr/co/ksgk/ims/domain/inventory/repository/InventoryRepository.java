package kr.co.ksgk.ims.domain.inventory.repository;

import kr.co.ksgk.ims.domain.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @EntityGraph(attributePaths = {"product", "product.brand", "product.brand.company"})
    List<Inventory> findAll();

    @EntityGraph(attributePaths = {"product", "product.brand", "product.brand.company"})
    Optional<Inventory> findById(Integer id);

}
