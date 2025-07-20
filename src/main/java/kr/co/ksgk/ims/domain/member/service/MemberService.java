package kr.co.ksgk.ims.domain.member.service;

import kr.co.ksgk.ims.domain.member.dto.MemberLoginResponseDto;
import kr.co.ksgk.ims.domain.member.dto.MemberSignupRequestDto;
import kr.co.ksgk.ims.domain.member.dto.MemberLoginRequestDto;
import kr.co.ksgk.ims.domain.member.dto.MemberInfoResponseDto;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.jwt.JwtProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import kr.co.ksgk.ims.domain.member.dto.SimpleInfoDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService
{
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

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

        String token=jwtProvider.createToken(member.getId());

        return new MemberLoginResponseDto(member.getId(),member.getRole(),token);
    }

    public MemberInfoResponseDto getMyInfo()
    {
        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Member member=memberRepository.findById(id).orElseThrow(()-> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        List<SimpleInfoDto> companyDtos = member.getMemberCompanies().stream()
                .map(mc -> new SimpleInfoDto(mc.getCompany().getId(), mc.getCompany().getName()))
                .toList();

        List<SimpleInfoDto> brandDtos = member.getMemberBrands().stream()
                .map(mb -> new SimpleInfoDto(mb.getBrand().getId(), mb.getBrand().getName()))
                .toList();

        return new MemberInfoResponseDto
                (
                    member.getId(),
                    member.getUsername(),
                    member.getName(),
                    member.getPhone(),
                    member.getRole(),
                    companyDtos,
                    brandDtos,
                    member.getNote(),
                    member.getCreatedAt().toString(),
                    member.getUpdatedAt().toString()
                );
    }

}
