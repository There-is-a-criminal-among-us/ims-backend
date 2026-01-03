package kr.co.ksgk.ims.domain.member.repository;

import kr.co.ksgk.ims.domain.member.entity.MemberBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBrandRepository extends JpaRepository<MemberBrand, Long> {

    @Query("SELECT mb FROM MemberBrand mb WHERE mb.member.id = :memberId")
    List<MemberBrand> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT CASE WHEN COUNT(mb) > 0 THEN true ELSE false END " +
            "FROM MemberBrand mb " +
            "WHERE mb.member.id = :memberId AND mb.brand.id = :brandId")
    boolean existsByMemberIdAndBrandId(@Param("memberId") Long memberId, @Param("brandId") Long brandId);
}