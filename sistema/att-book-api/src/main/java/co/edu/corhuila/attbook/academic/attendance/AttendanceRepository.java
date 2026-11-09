
package co.edu.corhuila.attbook.academic.attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    // Consultar las asistencias de una sesión
    List<Attendance> findBySessionIdOrderByRegisteredAtAsc(
            Long sessionId
    );

    // Consultar el historial de un estudiante
    List<Attendance> findByStudentIdOrderByRegisteredAtDesc(
            Long studentId
    );

    // Comprobar si ya registró asistencia
    boolean existsByStudentIdAndSessionId(
            Long studentId,
            Long sessionId
    );
}
