package co.edu.corhuila.attbook.academic.courses;

public record CourseResponse(
        Long id,
        String nombre,
        String codigo,
        Long profesorId
) {

    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getCode(),
                course.getTeacherId()
        );
    }
}