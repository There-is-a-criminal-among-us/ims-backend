package kr.co.ksgk.ims.domain.member.controller;

import kr.co.ksgk.ims.domain.member.dto.*;
import  kr.co.ksgk.ims.domain.member.service.MemberService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController
{
    private final MemberService memberService;

    @PostMapping
    ResponseEntity<SuccessResponse<?>> signup(@RequestBody MemberSignupRequestDto dto)
    {
        memberService.signup(dto);

        return SuccessResponse.ok("signup success");
    }

    @PostMapping("/login")
    ResponseEntity<SuccessResponse<?>> login(@RequestBody MemberLoginRequestDto dto)
    {
        MemberLoginResponseDto response=memberService.login(dto);

        return SuccessResponse.ok(response);
    }

    @PostMapping("/logout")
    ResponseEntity<SuccessResponse<?>> logout()
    {
        return  SuccessResponse.ok("logout success");
    }

    @GetMapping("/me")
    ResponseEntity<SuccessResponse<?>> getMyInfo()
    {
        return SuccessResponse.ok(memberService.getMyInfo());
    }

    @PutMapping("/{id}")
    ResponseEntity<SuccessResponse<?>> editMemberInfo(@PathVariable Long id,@RequestBody MemberEditRequestDto dto)
    {

        MemberInfoResponseDto response=memberService.editMemberInfo(id,dto);

        return SuccessResponse.ok(response);
    }

    @GetMapping("")
    ResponseEntity<SuccessResponse<?>> getAllMemberInfo(@RequestParam(required = false) String keyword)
    {
        List<MemberInfoResponseDto> result=memberService.getAllMemberInfo(keyword);

        return SuccessResponse.ok(result);
    }

    @PatchMapping("/{id}/change-password")
    ResponseEntity<SuccessResponse<?>> changePassword(@PathVariable Long id, @RequestBody MemberPasswordChangeRequestDto dto)
    {
        memberService.changePassword(id,dto);

        return SuccessResponse.noContent();
    }

    @PatchMapping("/{id}/reset-password")
    ResponseEntity<SuccessResponse<?>> resetPassword(@PathVariable Long id)
    {
        memberService.resetPassword(id);

        return SuccessResponse.noContent();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<SuccessResponse<?>> deleteMember(@PathVariable Long id)
    {
        memberService.deleteMember(id);

        return SuccessResponse.noContent();
    }
}
