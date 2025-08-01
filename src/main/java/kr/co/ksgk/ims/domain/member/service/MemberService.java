package kr.co.ksgk.ims.domain.member.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.company.entity.Company;
import kr.co.ksgk.ims.domain.company.repository.CompanyRepository;
import kr.co.ksgk.ims.domain.member.dto.request.ChangePasswordRequest;
import kr.co.ksgk.ims.domain.member.dto.request.MemberUpdateRequest;
import kr.co.ksgk.ims.domain.member.dto.response.MemberInfoResponse;
import kr.co.ksgk.ims.domain.member.dto.response.PagingMemberInfoResponse;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final BrandRepository brandRepository;

    public MemberInfoResponse getMemberInfoById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMemberInfo(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (request.name() != null) member.updateName(request.name());
        if (request.phone() != null) member.updatePhone(request.phone());
        if (request.note() != null) member.updateNote(request.note());
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMemberManagement(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (request.managingCompanies() != null && request.managingBrands() != null) {
            throw new BusinessException(ErrorCode.MEMBER_MANAGE_CONFLICT);
        }
        if (request.managingCompanies() != null) {
            List<Company> companies = companyRepository.findAllById(request.managingCompanies());
            member.updateMemberCompanies(companies);
        } else if (request.managingBrands() != null) {
            List<Brand> brands = brandRepository.findAllById(request.managingBrands());
            member.updateMemberBrands(brands);
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        return MemberInfoResponse.from(member);
    }

    public PagingMemberInfoResponse getMemberList(String search, Pageable pageable) {
        Page<Member> memberPage;
        if (search == null || search.isBlank()) {
            memberPage = memberRepository.findAll(pageable);
        } else {
            memberPage = memberRepository.findMemberByUsernameAndCompanyAndBrand(search, pageable);
        }
        List<MemberInfoResponse> memberInfoResponses = memberPage.getContent().stream()
                .map(MemberInfoResponse::from)
                .toList();
        return PagingMemberInfoResponse.of(memberPage, memberInfoResponses);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(request.oldPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        member.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }

    @Transactional
    public void resetPassword(Long memberId) {
        Member member=memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        member.changePassword(passwordEncoder.encode("1234"));
    }
}
