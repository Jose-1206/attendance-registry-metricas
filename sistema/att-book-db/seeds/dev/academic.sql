
-- =====================================================
-- ATTENDANCE REGISTRY
-- Academic demonstration data
-- Database: PostgreSQL
-- =====================================================

BEGIN;

-- =====================================================
-- 1. VALIDAR LOS USUARIOS DE DEMOSTRACION
-- =====================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM users
        WHERE email = 'teacher.owner@example.test'
          AND role = 'TEACHER'
    ) OR NOT EXISTS (
        SELECT 1 FROM users
        WHERE email = 'teacher.other@example.test'
          AND role = 'TEACHER'
    ) OR NOT EXISTS (
        SELECT 1 FROM users
        WHERE email = 'student.enrolled@example.test'
          AND role = 'STUDENT'
    ) OR NOT EXISTS (
        SELECT 1 FROM users
        WHERE email = 'student.unenrolled@example.test'
          AND role = 'STUDENT'
    ) THEN
        RAISE EXCEPTION
            'Missing demonstration users. Run identity.sql first.';
    END IF;
END
$$;

-- =====================================================
-- 2. CREAR LOS CURSOS
-- =====================================================

-- Curso 1: Desarrollo Fullstack

INSERT INTO courses (name, code, teacher_id)
SELECT
    'Desarrollo Fullstack',
    'DEMO-FS-01',
    u.id
FROM users u
WHERE u.email = 'teacher.owner@example.test'
  AND u.role = 'TEACHER'
  AND NOT EXISTS (
      SELECT 1 FROM courses
      WHERE code = 'DEMO-FS-01'
  );

-- Curso 2: Bases de Datos

INSERT INTO courses (name, code, teacher_id)
SELECT
    'Bases de Datos',
    'DEMO-BD-01',
    u.id
FROM users u
WHERE u.email = 'teacher.owner@example.test'
  AND u.role = 'TEACHER'
  AND NOT EXISTS (
      SELECT 1 FROM courses
      WHERE code = 'DEMO-BD-01'
  );

-- Curso 3: Ingenieria de Software

INSERT INTO courses (name, code, teacher_id)
SELECT
    'Ingenieria de Software',
    'DEMO-IS-01',
    u.id
FROM users u
WHERE u.email = 'teacher.other@example.test'
  AND u.role = 'TEACHER'
  AND NOT EXISTS (
      SELECT 1 FROM courses
      WHERE code = 'DEMO-IS-01'
  );

-- =====================================================
-- 3. VALIDAR LOS CURSOS
-- =====================================================

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM (
            VALUES
                ('DEMO-FS-01', 'teacher.owner@example.test'),
                ('DEMO-BD-01', 'teacher.owner@example.test'),
                ('DEMO-IS-01', 'teacher.other@example.test')
        ) AS expected(code, teacher_email)
        LEFT JOIN courses c
            ON c.code = expected.code
        LEFT JOIN users u
            ON u.id = c.teacher_id
        WHERE c.id IS NULL
           OR u.email IS DISTINCT FROM expected.teacher_email
    ) THEN
        RAISE EXCEPTION
            'Course fixtures conflict with existing data.';
    END IF;
END
$$;

-- =====================================================
-- 4. CREAR LAS INSCRIPCIONES
-- =====================================================

-- Inscribir al estudiante en:
-- Desarrollo Fullstack
-- Bases de Datos

INSERT INTO enrollments (
    student_id,
    course_id,
    enrollment_date
)
SELECT
    u.id,
    c.id,
    DATE '2026-11-01'
FROM users u
CROSS JOIN courses c
WHERE u.email = 'student.enrolled@example.test'
  AND u.role = 'STUDENT'
  AND c.code IN (
      'DEMO-FS-01',
      'DEMO-BD-01'
  )
ON CONFLICT (student_id, course_id) DO NOTHING;

-- =====================================================
-- 5. VALIDAR LAS INSCRIPCIONES
-- =====================================================

DO $$
BEGIN
    IF (
        SELECT COUNT(*)
        FROM enrollments e
        JOIN users u
            ON u.id = e.student_id
        JOIN courses c
            ON c.id = e.course_id
        WHERE u.email = 'student.enrolled@example.test'
          AND c.code IN ('DEMO-FS-01', 'DEMO-BD-01')
          AND e.enrollment_date = DATE '2026-11-01'
    ) <> 2 THEN
        RAISE EXCEPTION
            'Expected demonstration enrollments are missing or invalid.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM enrollments e
        JOIN users u
            ON u.id = e.student_id
        WHERE u.email = 'student.unenrolled@example.test'
    ) THEN
        RAISE EXCEPTION
            'The unenrolled demonstration student must have no enrollments.';
    END IF;
END
$$;

COMMIT;

-- =====================================================
-- END OF ACADEMIC DEMONSTRATION DATA
-- =====================================================
