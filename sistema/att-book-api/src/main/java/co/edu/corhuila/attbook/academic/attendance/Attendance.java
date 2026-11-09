
package co.edu.corhuila.attbook.academic.attendance;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "attendance_records")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    protected Attendance() {
        // Constructor requerido por JPA
    }

    public Attendance(
            Long studentId,
            Long sessionId,
            LocalDateTime registeredAt
    ) {
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}
