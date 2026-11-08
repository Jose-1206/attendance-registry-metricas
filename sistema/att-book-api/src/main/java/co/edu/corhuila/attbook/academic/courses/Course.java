package co.edu.corhuila.attbook.academic.courses;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    protected Course() {
        // Required by JPA.
    }

    public Course(String name, String code, Long teacherId) {
        this.name = name;
        this.code = code;
        this.teacherId = teacherId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void updateDetails(String name, String code) {
        this.name = name;
        this.code = code;
    }
}