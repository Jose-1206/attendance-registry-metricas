
package co.edu.corhuila.attbook.academic.sessions;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.courses.Course;
import co.edu.corhuila.attbook.academic.courses.CourseRepository;
import co.edu.corhuila.attbook.academic.enrollments.EnrollmentRepository;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public SessionService(
            SessionRepository sessionRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public SessionResponse create(
            SessionRequest request,
            SessionActor actor
    ) {
        requireRole(actor, "TEACHER");
        validateRequest(request);

        courseRepository.findByIdAndTeacherId(
                request.courseId(),
                actor.userId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Course not found"
        ));

        ClassSession session = new ClassSession(
                request.courseId(),
                request.title().trim(),
                request.description(),
                request.startAt(),
                request.endAt()
        );

        return SessionResponse.from(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public SessionResponse findById(
            Long sessionId,
            SessionActor actor
    ) {
        requireAuthenticated(actor);

        ClassSession session = findSession(sessionId);
        requireCourseAccess(session.getCourseId(), actor);

        return SessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> findByCourse(
            Long courseId,
            SessionActor actor
    ) {
        requireAuthenticated(actor);
        requireCourseAccess(courseId, actor);

        return sessionRepository
                .findByCourseIdOrderByStartAtAsc(courseId)
                .stream()
                .map(SessionResponse::from)
                .toList();
    }

    @Transactional
    public SessionResponse update(
            Long sessionId,
            SessionRequest request,
            SessionActor actor
    ) {
        requireRole(actor, "TEACHER");

        ClassSession session = findSession(sessionId);
        requireCourseAccess(session.getCourseId(), actor);
        validateRequest(request);

        if (!session.getCourseId().equals(request.courseId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A session cannot be moved to another course"
            );
        }

        session.updateDetails(
                request.title().trim(),
                request.description(),
                request.startAt(),
                request.endAt()
        );

        return SessionResponse.from(sessionRepository.save(session));
    }

    @Transactional
    public void delete(Long sessionId, SessionActor actor) {
        requireRole(actor, "TEACHER");

        ClassSession session = findSession(sessionId);
        requireCourseAccess(session.getCourseId(), actor);

        try {
            sessionRepository.delete(session);
            sessionRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The session cannot be deleted because it has related records",
                    exception
            );
        }
    }

    // NUEVO: VALIDAR PERMISOS DEL DOCENTE PARA GENERAR QR
    @Transactional(readOnly = true)
    public void validateTeacherForQr(
            Long sessionId,
            SessionActor actor
    ) {
        // Comprobar que sea un docente
        requireRole(actor, "TEACHER");

        // Comprobar que la sesión exista
        ClassSession session = findSession(sessionId);

        // Comprobar que el curso pertenezca al docente
        requireCourseAccess(session.getCourseId(), actor);
    }

    private ClassSession findSession(Long sessionId) {
        if (sessionId == null || sessionId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid session ID"
            );
        }

        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Session not found"
                ));
    }


private void requireCourseAccess(
        Long courseId,
        SessionActor actor
) {
    if (courseId == null || courseId <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid course ID"
        );
    }

    Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            ));

    if ("TEACHER".equals(actor.role())) {
        requireTeacherCourseAccess(course, actor);
    } else if ("STUDENT".equals(actor.role())) {
        requireStudentCourseAccess(courseId, actor);
    } else {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Role not allowed"
        );
    }
}

private void requireTeacherCourseAccess(
        Course course,
        SessionActor actor
) {
    if (!course.getTeacherId().equals(actor.userId())) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Access denied"
        );
    }
}

private void requireStudentCourseAccess(
        Long courseId,
        SessionActor actor
) {
    boolean enrolled =
            enrollmentRepository.existsByStudentIdAndCourseId(
                    actor.userId(),
                    courseId
            );

    if (!enrolled) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Student is not enrolled in this course"
        );
    }
}

private void validateRequest(SessionRequest request) {
    if (request == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Session data is required"
        );
    }

    validateCourseId(request.courseId());
    validateTitle(request.title());
    validateDescription(request.description());
    validateDates(request);
}

private void validateCourseId(Long courseId) {
    if (courseId == null || courseId <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid course ID"
        );
    }
}

private void validateTitle(String title) {
    if (title == null
            || title.isBlank()
            || title.length() > 150) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid session title"
        );
    }
}

private void validateDescription(String description) {
    if (description != null
            && description.length() > 500) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Session description is too long"
        );
    }
}

private void validateDates(SessionRequest request) {
    if (request.startAt() == null
            || request.endAt() == null
            || !request.endAt().isAfter(request.startAt())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "End time must be after start time"
        );
    }
}


    private void requireRole(SessionActor actor, String role) {
        requireAuthenticated(actor);

        if (!role.equals(actor.role())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Role not allowed"
            );
        }
    }

    private void requireAuthenticated(SessionActor actor) {
        if (actor == null
                || actor.userId() == null
                || actor.userId() <= 0
                || actor.role() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }
    }
}
