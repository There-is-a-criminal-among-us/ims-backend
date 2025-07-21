package kr.co.ksgk.ims.domain.member.controller;

import kr.co.ksgk.ims.domain.member.dto.MemberInfoResponseDto;
import kr.co.ksgk.ims.domain.member.dto.MemberLoginResponseDto;
import kr.co.ksgk.ims.domain.member.dto.MemberSignupRequestDto;
import kr.co.ksgk.ims.domain.member.dto.MemberLoginRequestDto;
import kr.co.ksgk.ims.domain.member.dto.MemberEditRequestDto;
import  kr.co.ksgk.ims.domain.member.service.MemberService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController
{
    private final MemberService memberService;

    @PostMapping
    ResponseEntity<SuccessResponse<?>> signup(MemberSignupRequestDto dto)
    {
        memberService.signup(dto);

        return SuccessResponse.ok("signup success");
    }

    @PostMapping("/login")
    ResponseEntity<SuccessResponse<?>> login(MemberLoginRequestDto dto)
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
    ResponseEntity<SuccessResponse<?>> editMemberInfo(@PathVariable Long id,MemberEditRequestDto dto)
    {

        MemberInfoResponseDto response=memberService.editMemberInfo(id,dto);

        return SuccessResponse.ok(response);
    }
}
