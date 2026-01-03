package kr.co.ksgk.ims.domain.attendance.dto.response;

public record AttendanceTokenResponse(
        String token
) {
    public static AttendanceTokenResponse from(String token) {
        return new AttendanceTokenResponse(token);
    }
}
