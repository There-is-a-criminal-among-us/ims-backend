package kr.co.ksgk.ims.domain.member.controller;

import kr.co.ksgk.ims.domain.member.dto.MemberSignupRequestDto;
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
    ResponseEntity<SuccessResponse<?>> signup(MemberSignupRequestDto memberSignupRequestDto)
    {
        memberService.signup(memberSignupRequestDto);

        return SuccessResponse.ok("회원가입 완료");
    }

}
