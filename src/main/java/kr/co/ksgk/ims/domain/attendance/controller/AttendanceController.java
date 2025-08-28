package kr.co.ksgk.ims.domain.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.ksgk.ims.domain.attendance.dto.request.AttendanceRequest;
import kr.co.ksgk.ims.domain.attendance.dto.request.AttendanceUpdateRequest;
import kr.co.ksgk.ims.domain.attendance.dto.response.AttendanceResponse;
import kr.co.ksgk.ims.domain.attendance.dto.response.AttendanceTokenResponse;
import kr.co.ksgk.ims.domain.attendance.dto.response.PagingAttendanceResponse;
import kr.co.ksgk.ims.domain.attendance.service.AttendanceService;
import kr.co.ksgk.ims.global.annotation.Auth;
import kr.co.ksgk.ims.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendances")
@Tag(name = "Attendance", description = "출석 관련 API")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "출석 토큰 생성", description = "출석을 위한 토큰을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "출석 토큰 생성 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AttendanceTokenResponse.class)))
    @PreAuthorize("hasAnyRole('MEMBER', 'PART_TIME')")
    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<?>> createToken(@Auth Long memberId) {
        AttendanceTokenResponse response = attendanceService.createToken(memberId);
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "근무 시작", description = "근무를 시작합니다.")
    @ApiResponse(responseCode = "200", description = "근무 시작 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AttendanceResponse.class)))
    @PreAuthorize("hasRole('ATTENDANCE')")
    @PostMapping("/start")
    public ResponseEntity<SuccessResponse<?>> startShift(@RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.startShift(request);
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "근무 종료", description = "근무를 종료합니다.")
    @ApiResponse(responseCode = "200", description = "근무 종료 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AttendanceResponse.class)))
    @PreAuthorize("hasAnyRole('MEMBER', 'PART_TIME')")
    @PostMapping("/end")
    public ResponseEntity<SuccessResponse<?>> endShift(@Auth Long memberId) {
        AttendanceResponse response = attendanceService.endShift(memberId);
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "출석 목록 조회", description = "모든 출석 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "출석 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingAttendanceResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getAttendanceList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingAttendanceResponse response = attendanceService.getAttendanceList(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")));
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "내 출석 목록 조회", description = "로그인한 사용자의 출석 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 출석 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingAttendanceResponse.class)))
    @PreAuthorize("hasAnyRole('MEMBER', 'PART_TIME')")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<?>> getMyAttendanceList(
            @Auth Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingAttendanceResponse response = attendanceService.getMyAttendanceList(memberId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")));
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "출퇴근 시간 수정", description = "PART_TIME 사용자의 출퇴근 시간을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "출퇴근 시간 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AttendanceResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{attendanceId}")
    public ResponseEntity<SuccessResponse<?>> updatePartTimeAttendance(
            @PathVariable Long attendanceId,
            @RequestBody @Valid AttendanceUpdateRequest request
    ) {
        AttendanceResponse response = attendanceService.updatePartTimeAttendance(attendanceId, request);
        return SuccessResponse.ok(response);
    }

    @Operation(summary = "사용자 출석 기록 조회", description = "특정 사용자의 출석 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "출석 기록 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagingAttendanceResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<?>> getPartTimeAttendanceList(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingAttendanceResponse response = attendanceService.getPartTimeAttendanceList(memberId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date")));
        return SuccessResponse.ok(response);
    }
}
