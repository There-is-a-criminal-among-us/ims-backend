package kr.co.ksgk.ims.domain.member.repository;

import kr.co.ksgk.ims.domain.member.entity.MemberCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCompanyRepository extends JpaRepository<MemberCompany, Long> {

    @Query("SELECT mc FROM MemberCompany mc WHERE mc.member.id = :memberId")
    List<MemberCompany> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT CASE WHEN COUNT(mc) > 0 THEN true ELSE false END " +
            "FROM MemberCompany mc " +
            "JOIN mc.company c " +
            "JOIN Brand b ON b.company.id = c.id " +
            "WHERE mc.member.id = :memberId AND b.id = :brandId")
    boolean existsByMemberIdAndBrandIdThroughCompany(@Param("memberId") Long memberId, @Param("brandId") Long brandId);
}