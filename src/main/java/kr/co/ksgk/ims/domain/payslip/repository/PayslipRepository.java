package kr.co.ksgk.ims.domain.payslip.repository;

import kr.co.ksgk.ims.domain.payslip.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByMemberIdOrderByDateDesc(Long memberId);
    Optional<Payslip> findByMemberIdAndDate(Long memberId, String date);
    boolean existsByMemberIdAndDate(Long memberId, String date);
}
