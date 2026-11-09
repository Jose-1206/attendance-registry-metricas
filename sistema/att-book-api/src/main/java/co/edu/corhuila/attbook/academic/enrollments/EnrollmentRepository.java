
package co.edu.corhuila.attbook.academic.enrollments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);
}
