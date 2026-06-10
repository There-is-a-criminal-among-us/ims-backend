package kr.co.ksgk.ims.domain.payslip.service;

import kr.co.ksgk.ims.domain.S3.service.S3Service;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.domain.payslip.dto.request.CreatePayslipRequest;
import kr.co.ksgk.ims.domain.payslip.dto.request.UpdatePayslipRequest;
import kr.co.ksgk.ims.domain.payslip.dto.response.PayslipListResponse;
import kr.co.ksgk.ims.domain.payslip.dto.response.PayslipResponse;
import kr.co.ksgk.ims.domain.payslip.entity.Payslip;
import kr.co.ksgk.ims.domain.payslip.repository.PayslipRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.ConflictException;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import kr.co.ksgk.ims.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Transactional
    public PayslipResponse createPayslip(CreatePayslipRequest request) {
        if (payslipRepository.existsByMemberIdAndDate(request.memberId(), request.date())) {
            throw new ConflictException(ErrorCode.PAYSLIP_ALREADY_EXISTS);
        }
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Payslip payslip = Payslip.builder()
                .member(member)
                .date(request.date())
                .s3Key(request.s3Key())
                .build();
        return PayslipResponse.from(payslipRepository.save(payslip), s3Service);
    }

    @Transactional
    public PayslipResponse updatePayslip(Long payslipId, UpdatePayslipRequest request) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYSLIP_NOT_FOUND));
        if (!payslip.getMember().getId().equals(request.memberId())
                && payslipRepository.existsByMemberIdAndDate(request.memberId(), payslip.getDate())) {
            throw new ConflictException(ErrorCode.PAYSLIP_ALREADY_EXISTS);
        }
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        String oldKey = payslip.getS3Key();
        payslip.update(member, request.s3Key());
        if (!oldKey.equals(request.s3Key())) {
            s3Service.deleteObject(oldKey);
        }
        return PayslipResponse.from(payslip, s3Service);
    }

    @Transactional
    public void deletePayslip(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYSLIP_NOT_FOUND));
        s3Service.deleteObject(payslip.getS3Key());
        payslipRepository.delete(payslip);
    }

    public PayslipListResponse getPayslipsByMember(Long requesterId, Long memberId, boolean isAdmin) {
        if (!isAdmin && !requesterId.equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        List<Payslip> payslips = payslipRepository.findByMemberIdOrderByDateDesc(memberId);
        return PayslipListResponse.from(payslips, s3Service);
    }

    public PayslipResponse getPayslip(Long requesterId, Long memberId, String date, boolean isAdmin) {
        if (!isAdmin && !requesterId.equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        Payslip payslip = payslipRepository.findByMemberIdAndDate(memberId, date)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYSLIP_NOT_FOUND));
        return PayslipResponse.from(payslip, s3Service);
    }
}
