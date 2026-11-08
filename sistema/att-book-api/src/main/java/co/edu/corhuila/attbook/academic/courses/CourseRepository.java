package co.edu.corhuila.attbook.academic.courses;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByTeacherIdOrderByIdAsc(Long teacherId);

    Optional<Course> findByIdAndTeacherId(Long id, Long teacherId);
}