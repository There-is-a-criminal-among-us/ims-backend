package kr.co.ksgk.ims.domain.payslip.controller;

import kr.co.ksgk.ims.domain.payslip.dto.request.CreatePayslipRequest;
import kr.co.ksgk.ims.domain.payslip.dto.request.UpdatePayslipRequest;
import kr.co.ksgk.ims.domain.payslip.dto.response.PayslipListResponse;
import kr.co.ksgk.ims.domain.payslip.dto.response.PayslipResponse;
import kr.co.ksgk.ims.domain.payslip.service.PayslipService;
import jakarta.validation.Valid;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payslips")
public class PayslipController implements PayslipApi {

    private final PayslipService payslipService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createPayslip(@Valid @RequestBody CreatePayslipRequest request) {
        PayslipResponse response = payslipService.createPayslip(request);
        return SuccessResponse.created(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{payslipId}")
    public ResponseEntity<SuccessResponse<?>> updatePayslip(
            @PathVariable Long payslipId,
            @Valid @RequestBody UpdatePayslipRequest request) {
        PayslipResponse response = payslipService.updatePayslip(payslipId, request);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{payslipId}")
    public ResponseEntity<SuccessResponse<?>> deletePayslip(@PathVariable Long payslipId) {
        payslipService.deletePayslip(payslipId);
        return SuccessResponse.noContent();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PART_TIME')")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<SuccessResponse<?>> getPayslipsByMember(
            @Auth Long requesterId,
            @PathVariable Long memberId,
            Authentication authentication) {
        boolean isAdmin = isAdminOrManager(authentication);
        PayslipListResponse response = payslipService.getPayslipsByMember(requesterId, memberId, isAdmin);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PART_TIME')")
    @GetMapping("/member/{memberId}/{date}")
    public ResponseEntity<SuccessResponse<?>> getPayslip(
            @Auth Long requesterId,
            @PathVariable Long memberId,
            @PathVariable String date,
            Authentication authentication) {
        boolean isAdmin = isAdminOrManager(authentication);
        PayslipResponse response = payslipService.getPayslip(requesterId, memberId, date, isAdmin);
        return SuccessResponse.ok(response);
    }

    private boolean isAdminOrManager(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
    }
}
