package co.edu.corhuila.attbook.academic.courses;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

@Service
@Validated
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository repository;

    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    public List<CourseResponse> list(CourseActor actor) {
        Long teacherId = requireTeacher(actor);

        return repository.findAllByTeacherIdOrderByIdAsc(teacherId)
                .stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseResponse findById(Long id, CourseActor actor) {
        Long teacherId = requireTeacher(actor);
        return CourseResponse.from(findOwnedCourse(id, teacherId));
    }

    @Transactional
    public CourseResponse create(
            @NotNull @Valid CourseRequest request,
            CourseActor actor
    ) {
        Long teacherId = requireTeacher(actor);

        Course course = new Course(
                request.nombre().strip(),
                request.codigo().strip(),
                teacherId
        );

        return CourseResponse.from(repository.save(course));
    }

    @Transactional
    public CourseResponse update(
            Long id,
            @NotNull @Valid CourseRequest request,
            CourseActor actor
    ) {
        Long teacherId = requireTeacher(actor);
        Course course = findOwnedCourse(id, teacherId);

        course.updateDetails(
                request.nombre().strip(),
                request.codigo().strip()
        );

        return CourseResponse.from(repository.save(course));
    }

    @Transactional
    public void delete(Long id, CourseActor actor) {
        Long teacherId = requireTeacher(actor);
        Course course = findOwnedCourse(id, teacherId);

        repository.delete(course);
        repository.flush();
    }

    private Course findOwnedCourse(Long id, Long teacherId) {
        return repository.findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));
    }

    private Long requireTeacher(CourseActor actor) {
        if (actor == null || actor.userId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        if (!"TEACHER".equals(actor.role())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Teacher role is required"
            );
        }

        return actor.userId();
    }
}