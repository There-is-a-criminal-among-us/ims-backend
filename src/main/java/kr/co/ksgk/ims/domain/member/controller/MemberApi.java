package kr.co.ksgk.ims.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.ksgk.ims.domain.auth.dto.request.SignupRequest;
import kr.co.ksgk.ims.domain.auth.dto.response.MemberResponse;
import kr.co.ksgk.ims.domain.member.dto.request.ChangePasswordRequest;
import kr.co.ksgk.ims.domain.member.dto.request.MemberUpdateRequest;
import kr.co.ksgk.ims.domain.member.dto.response.MemberInfoResponse;
import kr.co.ksgk.ims.domain.member.dto.response.PagingMemberInfoResponse;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 API")
public interface MemberApi {

    @Operation(
            summary = "회원 생성"
    )
    @ApiResponse(responseCode = "201", description = "회원 생성 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> signup(@RequestBody SignupRequest request);

    @Operation(
            summary = "회원 정보 수정",
            description = "회원 정보를 수정합니다. 관리자 권한이 필요합니다.  \n" +
                    "(관리할 브랜드/사업자 정보는 수정할 수 없음)"
    )
    @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> updateMemberInfo(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request);

    @Operation(
            summary = "관리할 브랜드/사업자 수정",
            description = "관리할 브랜드/사업자를 수정합니다. 관리자 권한이 필요합니다.  \n" +
                    "(회원 정보는 수정할 수 없음)"
    )
    @ApiResponse(responseCode = "200", description = "관리할 브랜드/사업자 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> updateMemberManagement(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request);

    @Operation(
            summary = "회원 목록 조회",
            description = "회원 목록을 조회합니다. 관리자 권한이 필요합니다.  \n" +
                    "검색어로 username, 관리하는 브랜드/사업자를 기준으로 검색할 수 있습니다.  \n" +
                    "검색어가 없으면 전체 회원을 조회합니다. (Swagger 상 \"sort\" 필드 무시, page는 0부터 시작)"
    )
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingMemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getMemberList(@RequestParam(required = false) String search, Pageable pageable);

    @Operation(
            summary = "내 정보 조회",
            description = "내 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getMyInfo(@Auth Long memberId);

    @Operation(
            summary = "다른 회원 정보 조회",
            description = "본인이 아닌 회원을 번호로 조회합니다. 관리자 권한이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getMemberInfoById(@PathVariable Long memberId);

    @Operation(
            summary = "비밀번호 변경",
            description = "회원의 비밀번호를 변경합니다.  \n" +
                    "기존 비밀번호와 새 비밀번호를 모두 입력해야 합니다."
    )
    @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request);

    @Operation(
            summary = "회원 삭제",
            description = "회원을 삭제 표시합니다. 관리자 권한이 필요합니다.  \n" +
                    "회원이 관리하는 브랜드/사업자 정보는 삭제되지 않습니다."
    )
    @ApiResponse(responseCode = "204", description = "회원 삭제 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> deleteMember(@PathVariable Long memberId);
}
