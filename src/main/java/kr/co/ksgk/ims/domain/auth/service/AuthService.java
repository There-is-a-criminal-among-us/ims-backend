package kr.co.ksgk.ims.domain.auth.service;

import kr.co.ksgk.ims.domain.auth.dto.AuthDto;
import kr.co.ksgk.ims.domain.auth.dto.CustomUserDetails;
import kr.co.ksgk.ims.domain.auth.dto.request.LoginRequest;
import kr.co.ksgk.ims.domain.auth.dto.request.SignupRequest;
import kr.co.ksgk.ims.domain.auth.dto.response.MemberResponse;
import kr.co.ksgk.ims.domain.auth.dto.response.TokenResponse;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.BusinessException;
import kr.co.ksgk.ims.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberResponse signup(SignupRequest request) {
        Member member = request.toEntity(passwordEncoder);
        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        AuthDto authDto = AuthDto.of(member.getId(), member.getRole());
        CustomUserDetails customUserDetails = new CustomUserDetails(authDto);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        return TokenResponse.of(accessToken, refreshToken);
    }
}