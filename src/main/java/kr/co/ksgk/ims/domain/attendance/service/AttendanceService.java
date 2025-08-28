package kr.co.ksgk.ims.domain.attendance.service;

import kr.co.ksgk.ims.domain.attendance.dto.request.AttendanceRequest;
import kr.co.ksgk.ims.domain.attendance.dto.response.AttendanceResponse;
import kr.co.ksgk.ims.domain.attendance.dto.response.AttendanceTokenResponse;
import kr.co.ksgk.ims.domain.attendance.dto.response.PagingAttendanceResponse;
import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.domain.attendance.repository.AttendanceRepository;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.repository.MemberRepository;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.EntityNotFoundException;
import kr.co.ksgk.ims.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final JwtProvider jwtProvider;
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    // 출석 토큰 생성
    public AttendanceTokenResponse createToken(Long memberId) {
        String token = jwtProvider.generateAttendanceToken(memberId);
        return AttendanceTokenResponse.from(token);
    }

    // 근무 시작
    @Transactional
    public AttendanceResponse startShift(AttendanceRequest request) {
        Long memberId = jwtProvider.validateAttendanceToken(request.token());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if (attendanceRepository.existsByMemberAndDate(member, LocalDate.now())) {
            throw new EntityNotFoundException(ErrorCode.ATTENDANCE_ALREADY_EXISTS);
        }
        Attendance attendance = Attendance.builder()
                .member(member)
                .date(LocalDate.now())
                .startTime(LocalDateTime.now())
                .build();
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return AttendanceResponse.from(savedAttendance);
    }

    // 근무 종료
    @Transactional
    public AttendanceResponse endShift(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Attendance attendance = attendanceRepository.findByMemberAndDate(member, LocalDate.now())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ATTENDANCE_NOT_FOUND));
        attendance.updateEndTime(LocalDateTime.now());
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return AttendanceResponse.from(updatedAttendance);
    }

    // 출석 목록 조회
    public PagingAttendanceResponse getAttendanceList(Pageable pageable) {
        Page<Attendance> attendancePage = attendanceRepository.findAll(pageable);
        List<AttendanceResponse> attendanceResponses = attendancePage.getContent().stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
        return PagingAttendanceResponse.of(attendancePage, attendanceResponses);
    }

    // 내 출석 목록 조회
    public PagingAttendanceResponse getMyAttendanceList(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Page<Attendance> attendancePage = attendanceRepository.findAllByMember(member, pageable);
        List<AttendanceResponse> attendanceResponses = attendancePage.getContent().stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
        return PagingAttendanceResponse.of(attendancePage, attendanceResponses);
    }
}
