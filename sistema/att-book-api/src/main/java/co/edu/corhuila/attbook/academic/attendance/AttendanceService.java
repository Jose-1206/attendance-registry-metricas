
package co.edu.corhuila.attbook.academic.attendance;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.enrollments.EnrollmentRepository;
import co.edu.corhuila.attbook.academic.sessions.ClassSession;
import co.edu.corhuila.attbook.academic.sessions.SessionActor;
import co.edu.corhuila.attbook.academic.sessions.SessionRepository;
import co.edu.corhuila.attbook.academic.sessions.SessionService;
import co.edu.corhuila.attbook.academic.sessions.qr.QrCode;
import co.edu.corhuila.attbook.academic.sessions.qr.QrCodeRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QrCodeRepository qrCodeRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            SessionService sessionService,
            SessionRepository sessionRepository,
            EnrollmentRepository enrollmentRepository,
            QrCodeRepository qrCodeRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.qrCodeRepository = qrCodeRepository;
    }

    // CONSULTAR ASISTENCIAS DE UNA CLASE - DOCENTE
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findByClass(
            Long claseId,
            SessionActor actor
    ) {
        sessionService.validateTeacherForQr(claseId, actor);

        return attendanceRepository
                .findBySessionIdOrderByRegisteredAtAsc(claseId)
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    // REGISTRAR ASISTENCIA - ESTUDIANTE

@Transactional
public AttendanceResponse registerAttendance(
        AttendanceRequest request,
        SessionActor actor
) {
    requireStudent(actor);
    validateAttendanceRequest(request);

    QrCode qrCode = findValidQr(request.qr().trim());
    ClassSession session = findQrSession(qrCode);

    requireEnrollment(session, actor);
    requireNotRegistered(session, actor);

    return saveAttendance(session, actor);
}

private void validateAttendanceRequest(
        AttendanceRequest request
) {
    if (request == null
            || request.qr() == null
            || request.qr().isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "QR code is required"
        );
    }
}

private QrCode findValidQr(String token) {
    QrCode qrCode = qrCodeRepository
            .findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid QR code"
            ));

    if (!qrCode.isActive()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "QR code is inactive"
        );
    }

    if (!Instant.now().isBefore(qrCode.getExpiresAt())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "QR code has expired"
        );
    }

    return qrCode;
}

private ClassSession findQrSession(QrCode qrCode) {
    return sessionRepository
            .findById(qrCode.getSessionId())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Session not found"
            ));
}

private void requireEnrollment(
        ClassSession session,
        SessionActor actor
) {
    boolean enrolled = enrollmentRepository
            .existsByStudentIdAndCourseId(
                    actor.userId(),
                    session.getCourseId()
            );

    if (!enrolled) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Student is not enrolled in this course"
        );
    }
}

private void requireNotRegistered(
        ClassSession session,
        SessionActor actor
) {
    boolean alreadyRegistered = attendanceRepository
            .existsByStudentIdAndSessionId(
                    actor.userId(),
                    session.getId()
            );

    if (alreadyRegistered) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Attendance already registered"
        );
    }
}

private AttendanceResponse saveAttendance(
        ClassSession session,
        SessionActor actor
) {
    Attendance attendance = new Attendance(
            actor.userId(),
            session.getId(),
            LocalDateTime.now(ZoneOffset.UTC)
    );

    try {
        Attendance saved = attendanceRepository
                .saveAndFlush(attendance);

        return AttendanceResponse.from(saved);

    } catch (DataIntegrityViolationException exception) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Attendance could not be registered; "
                        + "a record may already exist",
                exception
        );
    }
}

    // CONSULTAR HISTORIAL PERSONAL DEL ESTUDIANTE
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findMyHistory(
            SessionActor actor
    ) {
        requireStudent(actor);

        return attendanceRepository
                .findByStudentIdOrderByRegisteredAtDesc(
                        actor.userId()
                )
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    // VALIDAR IDENTIDAD Y ROL ESTUDIANTE
    private void requireStudent(SessionActor actor) {

        if (actor == null
                || actor.userId() == null
                || actor.userId() <= 0
                || actor.role() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        if (!"STUDENT".equals(actor.role())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can access this feature"
            );
        }
    }
}
