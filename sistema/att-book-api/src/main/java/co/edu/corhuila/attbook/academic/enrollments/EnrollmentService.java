
package co.edu.corhuila.attbook.academic.enrollments;

import java.util.List;

import co.edu.corhuila.attbook.academic.courses.CourseRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Enrollment> listMine(EnrollmentActor actor) {
        Long studentId = requireStudent(actor);

        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional
    public Enrollment enroll(Long courseId, EnrollmentActor actor) {
        Long studentId = requireStudent(actor);

        if (courseId == null || courseId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid course ID is required"
            );
        }

        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(
                studentId,
                courseId
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student is already enrolled in this course"
            );
        }

        Enrollment enrollment = new Enrollment(studentId, courseId);

        try {
            return enrollmentRepository.saveAndFlush(enrollment);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Enrollment could not be created due to a data constraint",
                    exception
            );
        }
    }

    private Long requireStudent(EnrollmentActor actor) {
        if (actor == null || actor.userId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        if (!"STUDENT".equals(actor.role())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Student role is required"
            );
        }

        return actor.userId();
    }
}
