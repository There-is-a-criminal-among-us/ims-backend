package kr.co.ksgk.ims.domain.member.repository;

import kr.co.ksgk.ims.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>
{
    Optional<Member> findByUsername(String username);

    @Query("""
                SELECT DISTINCT m
                FROM Member m
                LEFT JOIN m.memberCompanies mc
                LEFT JOIN mc.company c
                WHERE m.username LIKE %:keyword%
                   OR c.name LIKE %:keyword%
            """)
    List<Member> searchByUsernameOrCompanyName(@Param("keyword") String keyword);
}