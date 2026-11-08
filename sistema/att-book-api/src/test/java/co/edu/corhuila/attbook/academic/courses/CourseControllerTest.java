package co.edu.corhuila.attbook.academic.courses;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseControllerTest {

    private CourseService service;
    private MockMvc mvc;

    private final CourseActor teacherActor =
            new CourseActor(10L, "TEACHER");

    private final CoursePrincipal teacherPrincipal = new CoursePrincipal() {

        @Override
        public Long getUserId() {
            return 10L;
        }

        @Override
        public String getRole() {
            return "TEACHER";
        }

        @Override
        public String getName() {
            return "teacher@example.test";
        }
    };

    @BeforeEach
    void setUp() {
        service = mock(CourseService.class);

        mvc = MockMvcBuilders
                .standaloneSetup(new CourseController(service))
                .setControllerAdvice(new CourseExceptionHandler())
                .build();
    }

    @Test
    void rejectsRequestWithoutAuthentication() throws Exception {
        mvc.perform(get("/cursos"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsPrincipalWithoutVerifiedCourseIdentity() throws Exception {
        mvc.perform(get("/cursos")
                        .principal(() -> "unverified-user"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void returnsCourseList() throws Exception {
        when(service.list(teacherActor)).thenReturn(List.of(
                new CourseResponse(1L, "Fullstack", "FS-01", 10L)
        ));

        mvc.perform(get("/cursos")
                        .principal(teacherPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Fullstack"))
                .andExpect(jsonPath("$[0].profesorId").value(10));

        verify(service).list(teacherActor);
    }

    @Test
    void createsCourseAndReturnsLocation() throws Exception {
        CourseRequest request = new CourseRequest("Fullstack", "FS-01");

        when(service.create(request, teacherActor)).thenReturn(
                new CourseResponse(1L, "Fullstack", "FS-01", 10L)
        );

        mvc.perform(post("/cursos")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Fullstack",
                                  "codigo": "FS-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/cursos/1"
                ))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("FS-01"));

        verify(service).create(request, teacherActor);
    }

    @Test
    void rejectsBlankCourseName() throws Exception {
        mvc.perform(post("/cursos")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "   ",
                                  "codigo": "FS-01"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsMissingCourseCode() throws Exception {
        mvc.perform(post("/cursos")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Fullstack"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void returnsNotFoundForUnavailableCourse() throws Exception {
        when(service.findById(99L, teacherActor)).thenThrow(
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                )
        );

        mvc.perform(get("/cursos/99")
                        .principal(teacherPrincipal))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatesCourse() throws Exception {
        CourseRequest request = new CourseRequest("Updated", "UP-01");

        when(service.update(1L, request, teacherActor)).thenReturn(
                new CourseResponse(1L, "Updated", "UP-01", 10L)
        );

        mvc.perform(put("/cursos/1")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Updated",
                                  "codigo": "UP-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Updated"));

        verify(service).update(1L, request, teacherActor);
    }

    @Test
    void deletesCourse() throws Exception {
        mvc.perform(delete("/cursos/1")
                        .principal(teacherPrincipal))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(1L, teacherActor);
    }

    @Test
    void returnsConflictWhenDeletionViolatesDatabaseConstraints()
            throws Exception {

        doThrow(new DataIntegrityViolationException(
                "Internal database detail"
        )).when(service).delete(1L, teacherActor);

        mvc.perform(delete("/cursos/1")
                        .principal(teacherPrincipal))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(
                        "The operation conflicts with existing data or related records."
                ));
    }
}