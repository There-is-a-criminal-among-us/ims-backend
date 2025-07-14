package kr.co.ksgk.ims.domain.member.repository;

import kr.co.ksgk.ims.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>
{
    boolean existsByPhone(String phone); // 이메일 중복 체크용 (선택)
}