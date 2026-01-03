package kr.co.ksgk.ims.domain.attendance.repository;

import kr.co.ksgk.ims.domain.attendance.entity.Attendance;
import kr.co.ksgk.ims.domain.member.entity.Member;
import kr.co.ksgk.ims.domain.member.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByMemberAndDate(Member member, LocalDate date);

    Page<Attendance> findAllByMember(Member member, Pageable pageable);

    Page<Attendance> findAllByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByMemberAndDate(Member member, LocalDate date);

    @Query("SELECT a FROM Attendance a JOIN a.member m WHERE m.role = :role AND a.date = :date AND a.startTime IS NOT NULL")
    Page<Attendance> findTodayWorkingMembersByRole(@Param("role") Role role, @Param("date") LocalDate date, Pageable pageable);
}
