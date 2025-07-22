package kr.co.ksgk.ims.domain.member.controller;

import kr.co.ksgk.ims.domain.member.dto.request.ChangePasswordRequest;
import kr.co.ksgk.ims.domain.member.dto.request.MemberUpdateRequest;
import kr.co.ksgk.ims.domain.member.dto.response.MemberInfoResponse;
import kr.co.ksgk.ims.domain.member.dto.response.PagingMemberInfoResponse;
import kr.co.ksgk.ims.domain.member.service.MemberService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{memberId}")
    ResponseEntity<SuccessResponse<?>> updateMemberInfo(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        MemberInfoResponse response = memberService.updateMemberInfo(memberId, request);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{memberId}/management")
    ResponseEntity<SuccessResponse<?>> updateMemberManagement(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        MemberInfoResponse response = memberService.updateMemberManagement(memberId, request);
        return SuccessResponse.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    ResponseEntity<SuccessResponse<?>> getMemberList(@RequestParam(required = false) String search, Pageable pageable) {
        PagingMemberInfoResponse pagingMemberInfoResponse = memberService.getMemberList(search, pageable);
        return SuccessResponse.ok(pagingMemberInfoResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{memberId}")
    ResponseEntity<SuccessResponse<?>> getMemberInfoById(@PathVariable Long memberId) {
        MemberInfoResponse memberInfoResponse = memberService.getMemberInfoById(memberId);
        return SuccessResponse.ok(memberInfoResponse);
    }

    @PatchMapping("/{id}/password")
    ResponseEntity<SuccessResponse<?>> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        memberService.changePassword(id, request);
        return SuccessResponse.noContent();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{memberId}")
    ResponseEntity<SuccessResponse<?>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return SuccessResponse.noContent();
    }
}
