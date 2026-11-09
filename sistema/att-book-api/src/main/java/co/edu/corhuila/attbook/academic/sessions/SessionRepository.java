
package co.edu.corhuila.attbook.academic.sessions;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<ClassSession, Long> {

    List<ClassSession> findByCourseIdOrderByStartAtAsc(Long courseId);

}
