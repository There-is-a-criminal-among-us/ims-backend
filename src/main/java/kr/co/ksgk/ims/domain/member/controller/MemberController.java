package kr.co.ksgk.ims.domain.member.controller;

import kr.co.ksgk.ims.domain.auth.dto.request.SignupRequest;
import kr.co.ksgk.ims.domain.auth.dto.response.MemberResponse;
import kr.co.ksgk.ims.domain.auth.service.AuthService;
import kr.co.ksgk.ims.domain.member.dto.request.ChangePasswordRequest;
import kr.co.ksgk.ims.domain.member.dto.request.MemberUpdateRequest;
import kr.co.ksgk.ims.domain.member.dto.response.MemberInfoResponse;
import kr.co.ksgk.ims.domain.member.dto.response.PagingMemberInfoResponse;
import kr.co.ksgk.ims.domain.member.service.MemberService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberApi {

    private final MemberService memberService;
    private final AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> signup(@RequestBody SignupRequest request) {
        MemberResponse memberResponse = authService.signup(request);
        return SuccessResponse.created(memberResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<?>> updateMemberInfo(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        MemberInfoResponse memberInfoResponse = memberService.updateMemberInfo(memberId, request);
        return SuccessResponse.ok(memberInfoResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{memberId}/management")
    public ResponseEntity<SuccessResponse<?>> updateMemberManagement(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        MemberInfoResponse memberInfoResponse = memberService.updateMemberManagement(memberId, request);
        return SuccessResponse.ok(memberInfoResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getMemberList(@RequestParam(required = false) String search, Pageable pageable) {
        PagingMemberInfoResponse pagingMemberInfoResponse = memberService.getMemberList(search, pageable);
        return SuccessResponse.ok(pagingMemberInfoResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<?>> getMyInfo(@Auth Long memberId) {
        MemberInfoResponse memberInfoResponse = memberService.getMemberInfoById(memberId);
        return SuccessResponse.ok(memberInfoResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<?>> getMemberInfoById(@PathVariable Long memberId) {
        MemberInfoResponse memberInfoResponse = memberService.getMemberInfoById(memberId);
        return SuccessResponse.ok(memberInfoResponse);
    }

    @PatchMapping("/{memberId}/password")
    public ResponseEntity<SuccessResponse<?>> changePassword(@PathVariable Long memberId, @RequestBody ChangePasswordRequest request) {
        memberService.changePassword(memberId, request);
        return SuccessResponse.noContent();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<?>> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return SuccessResponse.noContent();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{memberId}/reset-password")
    public ResponseEntity<SuccessResponse<?>> resetPassword(@PathVariable Long memberId) {
        memberService.resetPassword(memberId);
        return SuccessResponse.noContent();
    }
}
