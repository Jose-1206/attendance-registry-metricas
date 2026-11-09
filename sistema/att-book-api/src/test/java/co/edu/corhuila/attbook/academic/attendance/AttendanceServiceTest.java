
package co.edu.corhuila.attbook.academic.attendance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.enrollments.EnrollmentRepository;
import co.edu.corhuila.attbook.academic.sessions.ClassSession;
import co.edu.corhuila.attbook.academic.sessions.SessionActor;
import co.edu.corhuila.attbook.academic.sessions.SessionRepository;
import co.edu.corhuila.attbook.academic.sessions.SessionService;
import co.edu.corhuila.attbook.academic.sessions.qr.QrCode;
import co.edu.corhuila.attbook.academic.sessions.qr.QrCodeRepository;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private QrCodeRepository qrCodeRepository;

    @InjectMocks
    private AttendanceService service;

    private final SessionActor student =
            new SessionActor(20L, "STUDENT");

    private final SessionActor teacher =
            new SessionActor(10L, "TEACHER");

    private final AttendanceRequest request =
            new AttendanceRequest(" qr-test ");

    private void validQr() {
        QrCode qrCode = new QrCode(
                7L,
                "qr-test",
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(3600)
        );

        when(qrCodeRepository.findByToken("qr-test"))
                .thenReturn(Optional.of(qrCode));
    }

    private void existingSession() {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        ClassSession session = new ClassSession(
                1L,
                "Programacion movil",
                "Clase practica",
                start,
                start.plusHours(2)
        );

        ReflectionTestUtils.setField(session, "id", 7L);

        when(sessionRepository.findById(7L))
                .thenReturn(Optional.of(session));
    }

    private void enrolledStudent() {
        when(enrollmentRepository
                .existsByStudentIdAndCourseId(20L, 1L))
                .thenReturn(true);
    }

    private void assertError(
            HttpStatus expectedStatus,
            String expectedReason,
            AttendanceRequest input,
            SessionActor actor
    ) {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.registerAttendance(input, actor)
        );

        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(expectedReason, exception.getReason());
    }

    @Test
    void studentCanRegisterValidAttendance() {
        validQr();
        existingSession();
        enrolledStudent();

        when(attendanceRepository.saveAndFlush(
                any(Attendance.class)
        )).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceResponse response =
                service.registerAttendance(request, student);

        assertEquals(20L, response.studentId());
        assertEquals(7L, response.claseId());

        verify(qrCodeRepository).findByToken("qr-test");
        verify(attendanceRepository)
                .saveAndFlush(any(Attendance.class));
    }

    @Test
    void rejectsNullRequest() {
        assertError(
                HttpStatus.BAD_REQUEST,
                "QR code is required",
                null,
                student
        );

        verifyNoInteractions(qrCodeRepository);
    }

    @Test
    void rejectsBlankQr() {
        assertError(
                HttpStatus.BAD_REQUEST,
                "QR code is required",
                new AttendanceRequest("   "),
                student
        );

        verifyNoInteractions(qrCodeRepository);
    }

    @Test
    void rejectsInvalidQr() {
        when(qrCodeRepository.findByToken("qr-test"))
                .thenReturn(Optional.empty());

        assertError(
                HttpStatus.BAD_REQUEST,
                "Invalid QR code",
                request,
                student
        );

        verifyNoInteractions(attendanceRepository);
    }

    @Test
    void rejectsInactiveQr() {
        QrCode qrCode = mock(QrCode.class);

        when(qrCodeRepository.findByToken("qr-test"))
                .thenReturn(Optional.of(qrCode));

        assertError(
                HttpStatus.BAD_REQUEST,
                "QR code is inactive",
                request,
                student
        );

        verifyNoInteractions(sessionRepository);
    }

    @Test
    void rejectsExpiredQr() {
        QrCode qrCode = new QrCode(
                7L,
                "qr-test",
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(3600)
        );

        when(qrCodeRepository.findByToken("qr-test"))
                .thenReturn(Optional.of(qrCode));

        assertError(
                HttpStatus.BAD_REQUEST,
                "QR code has expired",
                request,
                student
        );

        verifyNoInteractions(attendanceRepository);
    }

    @Test
    void rejectsMissingSession() {
        validQr();

        when(sessionRepository.findById(7L))
                .thenReturn(Optional.empty());

        assertError(
                HttpStatus.NOT_FOUND,
                "Session not found",
                request,
                student
        );
    }

    @Test
    void rejectsUnenrolledStudent() {
        validQr();
        existingSession();

        when(enrollmentRepository
                .existsByStudentIdAndCourseId(20L, 1L))
                .thenReturn(false);

        assertError(
                HttpStatus.FORBIDDEN,
                "Student is not enrolled in this course",
                request,
                student
        );

        verify(attendanceRepository, never())
                .saveAndFlush(any(Attendance.class));
    }

    @Test
    void rejectsDuplicateAttendance() {
        validQr();
        existingSession();
        enrolledStudent();

        when(attendanceRepository
                .existsByStudentIdAndSessionId(20L, 7L))
                .thenReturn(true);

        assertError(
                HttpStatus.CONFLICT,
                "Attendance already registered",
                request,
                student
        );

        verify(attendanceRepository, never())
                .saveAndFlush(any(Attendance.class));
    }

    @Test
    void rejectsTeacherRegistration() {
        assertError(
                HttpStatus.FORBIDDEN,
                "Only students can access this feature",
                request,
                teacher
        );

        verifyNoInteractions(qrCodeRepository);
    }

    @Test
    void rejectsUnauthenticatedUser() {
        assertError(
                HttpStatus.UNAUTHORIZED,
                "Authentication required",
                request,
                null
        );

        verifyNoInteractions(qrCodeRepository);
    }

    @Test
    void handlesDatabaseConflict() {
        validQr();
        existingSession();
        enrolledStudent();

        when(attendanceRepository.saveAndFlush(
                any(Attendance.class)
        )).thenThrow(
                new DataIntegrityViolationException("Duplicate record")
        );

        assertError(
                HttpStatus.CONFLICT,
                "Attendance could not be registered; "
                        + "a record may already exist",
                request,
                student
        );
    }
}
