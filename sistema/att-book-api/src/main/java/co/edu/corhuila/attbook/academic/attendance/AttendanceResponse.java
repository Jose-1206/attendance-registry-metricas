
package co.edu.corhuila.attbook.academic.attendance;

import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long studentId,
        Long claseId,
        LocalDateTime registeredAt
) {

    public static AttendanceResponse from(Attendance attendance) {
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getStudentId(),
                attendance.getSessionId(),
                attendance.getRegisteredAt()
        );
    }
}
