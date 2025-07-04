package kr.co.ksgk.ims.domain.product.repository;

import kr.co.ksgk.ims.domain.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @EntityGraph(attributePaths = {"brand", "brand.company"})
    List<Product> findAll();
}
