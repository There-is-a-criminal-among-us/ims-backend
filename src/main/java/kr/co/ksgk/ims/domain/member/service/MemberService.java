package kr.co.ksgk.ims.domain.member.service;

import kr.co.ksgk.ims.domain.member.dto.MemberLoginResponseDto;
import kr.co.ksgk.ims.domain.member.dto.MemberSignupRequestDto;
import kr.co.ksgk.ims.domain.member.dto.MemberLoginRequestDto;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
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
                .role(dto.getRole())
                .build();

        memberRepository.save(member);
    }

    public MemberLoginResponseDto login(MemberLoginRequestDto dto)
    {
        Member member=memberRepository.findByUsername(dto.getUsername())
                .orElseThrow(()-> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if(!dto.getPassword().equals(member.getPassword()))
        {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return new MemberLoginResponseDto(member.getId(), member.getRole());
    }
}
