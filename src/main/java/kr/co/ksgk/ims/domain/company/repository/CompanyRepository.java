package kr.co.ksgk.ims.domain.company.repository;

import kr.co.ksgk.ims.domain.company.entity.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    @EntityGraph(attributePaths = {"brands", "brands.products"})
    @Query("SELECT c FROM Company c")
    Set<Company> findAllWithBrandsAndProducts();
}
