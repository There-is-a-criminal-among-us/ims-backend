package kr.co.ksgk.ims.domain.company.repository;

import kr.co.ksgk.ims.domain.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @EntityGraph(attributePaths = {"brands", "brands.products"})
    @Query("SELECT c FROM Company c")
    List<Company> findAllWithBrandsAndProducts();

    Page<Company> findByNameContaining(String name, Pageable pageable);
}
