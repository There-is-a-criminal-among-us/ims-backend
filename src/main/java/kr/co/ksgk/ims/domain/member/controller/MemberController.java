//Pull Request test

package kr.co.ksgk.ims.domain.member.controller;

import kr.co.ksgk.ims.domain.member.dto.MemberLoginResponseDto;
import kr.co.ksgk.ims.domain.member.dto.MemberSignupRequestDto;
import kr.co.ksgk.ims.domain.member.dto.MemberLoginRequestDto;
import  kr.co.ksgk.ims.domain.member.service.MemberService;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
