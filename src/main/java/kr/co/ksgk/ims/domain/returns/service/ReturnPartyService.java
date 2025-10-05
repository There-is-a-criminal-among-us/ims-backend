package kr.co.ksgk.ims.domain.returns.service;

import kr.co.ksgk.ims.domain.brand.entity.Brand;
import kr.co.ksgk.ims.domain.brand.repository.BrandRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.domain.member.repository.MemberBrandRepository;
import kr.co.ksgk.ims.domain.member.repository.MemberCompanyRepository;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.returns.dto.request.CreateReturnPartyRequest;
import kr.co.ksgk.ims.domain.returns.dto.request.UpdateReturnPartyRequest;
import kr.co.ksgk.ims.domain.returns.dto.response.ReturnPartyResponse;
import kr.co.ksgk.ims.domain.returns.entity.ReturnHandler;
import kr.co.ksgk.ims.domain.returns.entity.ReturnMall;
import kr.co.ksgk.ims.domain.returns.repository.ReturnHandlerRepository;
import kr.co.ksgk.ims.domain.returns.repository.ReturnMallRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import kr.co.ksgk.ims.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReturnPartyService {

    private final BrandRepository brandRepository;
    private final ReturnMallRepository returnMallRepository;
    private final ReturnHandlerRepository returnHandlerRepository;
    private final MemberBrandRepository memberBrandRepository;
    private final MemberCompanyRepository memberCompanyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReturnPartyResponse createReturnMall(Long memberId, CreateReturnPartyRequest request) {
        validateBrandAccess(memberId, request.brandId());
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        ReturnMall returnMall = request.toReturnMall(brand);
        ReturnMall savedReturnMall = returnMallRepository.save(returnMall);
        return ReturnPartyResponse.from(savedReturnMall);
    }

    @Transactional
    public ReturnPartyResponse createReturnHandler(Long memberId, CreateReturnPartyRequest request) {
        validateBrandAccess(memberId, request.brandId());
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        ReturnHandler returnHandler = request.toReturnHandler(brand);
        ReturnHandler savedReturnHandler = returnHandlerRepository.save(returnHandler);
        return ReturnPartyResponse.from(savedReturnHandler);
    }

    public List<ReturnPartyResponse> getReturnMallsByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<ReturnMall> malls;
        if (member.getRole() == Role.ADMIN) {
            malls = returnMallRepository.findAll();
        } else {
            List<Long> brandIds = getManagedBrandIds(memberId);
            malls = returnMallRepository.findByBrandIdIn(brandIds);
        }

        return malls.stream()
                .map(ReturnPartyResponse::from)
                .toList();
    }

    public List<ReturnPartyResponse> getReturnHandlersByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<ReturnHandler> handlers;
        if (member.getRole() == Role.ADMIN) {
            handlers = returnHandlerRepository.findAll();
        } else {
            List<Long> brandIds = getManagedBrandIds(memberId);
            handlers = returnHandlerRepository.findByBrandIdIn(brandIds);
        }

        return handlers.stream()
                .map(ReturnPartyResponse::from)
                .toList();
    }

    private List<Long> getManagedBrandIds(Long memberId) {
        List<Long> directBrandIds = memberBrandRepository.findByMemberId(memberId).stream()
                .map(mb -> mb.getBrand().getId())
                .toList();

        List<Long> companyBrandIds = memberCompanyRepository.findByMemberId(memberId).stream()
                .flatMap(mc -> mc.getCompany().getBrands().stream())
                .map(Brand::getId)
                .toList();

        return Stream.concat(directBrandIds.stream(), companyBrandIds.stream())
                .distinct()
                .toList();
    }

    @Transactional
    public ReturnPartyResponse updateReturnMall(Long memberId, Long mallId, UpdateReturnPartyRequest request) {
        ReturnMall returnMall = returnMallRepository.findById(mallId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        validateBrandAccess(memberId, returnMall.getBrand().getId());
        returnMall.updateName(request.name());
        return ReturnPartyResponse.from(returnMall);
    }

    @Transactional
    public ReturnPartyResponse updateReturnHandler(Long memberId, Long handlerId, UpdateReturnPartyRequest request) {
        ReturnHandler returnHandler = returnHandlerRepository.findById(handlerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        validateBrandAccess(memberId, returnHandler.getBrand().getId());
        returnHandler.updateName(request.name());
        return ReturnPartyResponse.from(returnHandler);
    }

    @Transactional
    public void deleteReturnMall(Long memberId, Long mallId) {
        ReturnMall returnMall = returnMallRepository.findById(mallId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        validateBrandAccess(memberId, returnMall.getBrand().getId());
        returnMallRepository.delete(returnMall);
    }

    @Transactional
    public void deleteReturnHandler(Long memberId, Long handlerId) {
        ReturnHandler returnHandler = returnHandlerRepository.findById(handlerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        validateBrandAccess(memberId, returnHandler.getBrand().getId());
        returnHandlerRepository.delete(returnHandler);
    }

    private void validateBrandAccess(Long memberId, Long brandId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.ADMIN) {
            return;
        }

        boolean hasDirectAccess = memberBrandRepository.existsByMemberIdAndBrandId(memberId, brandId);
        boolean hasCompanyAccess = memberCompanyRepository.existsByMemberIdAndBrandIdThroughCompany(memberId, brandId);

        if (!hasDirectAccess && !hasCompanyAccess) {
            throw new UnauthorizedException(ErrorCode.FORBIDDEN);
        }
    }
}
