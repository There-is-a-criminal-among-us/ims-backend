package kr.co.ksgk.ims.domain.member.repository;

import kr.co.ksgk.ims.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Page<Member> findAll(Pageable pageable);

    @Query("""
                SELECT DISTINCT m
                FROM Member m
                LEFT JOIN m.memberCompanies mc
                LEFT JOIN mc.company c
                LEFT JOIN m.memberBrands mb
                LEFT JOIN mb.brand b
                WHERE m.username LIKE %:keyword%
                   OR c.name LIKE %:keyword%
                   OR b.name LIKE %:keyword%
            """)
    Page<Member> findMemberByUsernameAndCompanyAndBrand(@Param("keyword") String keyword, Pageable pageable);
}