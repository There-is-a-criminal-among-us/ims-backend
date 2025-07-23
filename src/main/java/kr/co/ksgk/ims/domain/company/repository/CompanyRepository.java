package kr.co.ksgk.ims.domain.company.repository;

import kr.co.ksgk.ims.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}