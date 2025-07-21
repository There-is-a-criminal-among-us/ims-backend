package kr.co.ksgk.ims.domain.member.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.member.dto.*;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.MemberBrand;
import kr.co.ksgk.ims.domain.member.entity.MemberCompany;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.jwt.JwtProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService
{
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final BrandRepository brandRepository;
    private final JwtProvider jwtProvider;

    private MemberInfoResponseDto memberToResponseDto(Member member)
    {
        List<SimpleInfoDto> companyDtos = member.getMemberCompanies().stream()
                .map(mc -> new SimpleInfoDto(mc.getCompany().getId(), mc.getCompany().getName()))
                .toList();

        List<SimpleInfoDto> brandDtos = member.getMemberBrands().stream()
                .map(mb -> new SimpleInfoDto(mb.getBrand().getId(), mb.getBrand().getName()))
                .toList();

        return new MemberInfoResponseDto(
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
        Member member = memberRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!dto.getPassword().equals(member.getPassword()))
        {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        String token = jwtProvider.createToken(member.getId());

        return new MemberLoginResponseDto(member.getId(), member.getRole(), token);
    }

    public MemberInfoResponseDto getMyInfo()
    {
        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Member member = memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        List<SimpleInfoDto> managingCompanies = member.getMemberCompanies().stream()
                .map(mc -> new SimpleInfoDto(mc.getCompany().getId(), mc.getCompany().getName()))
                .toList();

        List<SimpleInfoDto> managingBrands = member.getMemberBrands().stream()
                .map(mb -> new SimpleInfoDto(mb.getBrand().getId(), mb.getBrand().getName()))
                .toList();

        return new MemberInfoResponseDto
                (
                        member.getId(),
                        member.getUsername(),
                        member.getName(),
                        member.getPhone(),
                        member.getRole(),
                        managingCompanies,
                        managingBrands,
                        member.getNote(),
                        member.getCreatedAt().toString(),
                        member.getUpdatedAt().toString()
                );
    }

    public MemberInfoResponseDto getMemberInfoById(Long id)
    {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        List<SimpleInfoDto> managingCompanies = member.getMemberCompanies().stream()
                .map(mc -> new SimpleInfoDto(mc.getCompany().getId(), mc.getCompany().getName()))
                .toList();

        List<SimpleInfoDto> managingBrands = member.getMemberBrands().stream()
                .map(mb -> new SimpleInfoDto(mb.getBrand().getId(), mb.getBrand().getName()))
                .toList();

        return new MemberInfoResponseDto(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getPhone(),
                member.getRole(),
                managingCompanies,
                managingBrands,
                member.getNote(),
                member.getCreatedAt().toString(),
                member.getUpdatedAt().toString()
        );
    }

    public MemberInfoResponseDto editMemberInfo(Long id, MemberEditRequestDto dto)
    {
        Member member = memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        member.getMemberCompanies().clear();
        member.getMemberBrands().clear();

        List<Company> companyList = companyRepository.findAllById(dto.getManagingCompanies());
        List<Brand> brandList = brandRepository.findAllById(dto.getManagingBrands());

        for (Company company : companyList)
        {
            MemberCompany mc = new MemberCompany(member, company);
            member.getMemberCompanies().add(mc);
        }

        for (Brand brand : brandList)
        {
            MemberBrand mb = new MemberBrand(member, brand);
            member.getMemberBrands().add(mb);
        }

        member.update(dto.getName(), dto.getPhone(), dto.getNote());

        memberRepository.save(member);

        return getMemberInfoById(id);

    }


    public List<MemberInfoResponseDto> getAllMemberInfo(String keyword)
    {
        List<Member> members;

        if (keyword == null || keyword.isBlank())
        {
            members = memberRepository.findAll();
        }
        else
        {
            members = memberRepository.searchByUsernameOrCompanyName(keyword);
        }

        return members.stream().map(this::memberToResponseDto).toList();
    }

    public void changePassword(Long id, MemberPasswordChangeRequestDto dto)
    {
        Member member = memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!dto.getOldPassword().equals(member.getPassword()))
        {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        member.changePassword(dto.getNewPassword());
    }

    public void resetPassword(Long id)
    {
        Member member=memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        member.resetPassword();
    }

    public void deleteMember(Long id)
    {
        Member member=memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

}
