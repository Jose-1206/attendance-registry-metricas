
package co.edu.corhuila.attbook.academic.sessions;

import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        Long courseId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt
) {

    public static SessionResponse from(ClassSession session) {
        return new SessionResponse(
                session.getId(),
                session.getCourseId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartAt(),
                session.getEndAt()
        );
    }
}
