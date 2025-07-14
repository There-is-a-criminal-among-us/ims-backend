package kr.co.ksgk.ims.domain.member.service;

import kr.co.ksgk.ims.domain.member.dto.MemberSignupRequestDto;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService
{
    private final MemberRepository memberRepository;

    public void signup(MemberSignupRequestDto dto)
    {
        Member member = Member.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .name(dto.getName())
                .phone(dto.getPhone())
                .note(dto.getNote())
                .build();

        memberRepository.save(member);
    }
}
