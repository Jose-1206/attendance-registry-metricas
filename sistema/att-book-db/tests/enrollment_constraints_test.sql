
-- Attendance Registry
-- Database tests: Enrollment business rules
-- Requires migrations V1 and V2 to be applied first.
-- All test data is rolled back after execution.

BEGIN;

DO $$
DECLARE
    student_id BIGINT;
    teacher_id BIGINT;
    course_id BIGINT;
BEGIN
    -- Create temporary test users.
    INSERT INTO users (name, email, password_hash, role)
    VALUES (
        'Database Test Student',
        'db-test-student@example.test',
        'test-hash',
        'STUDENT'
    )
    RETURNING id INTO student_id;

    INSERT INTO users (name, email, password_hash, role)
    VALUES (
        'Database Test Teacher',
        'db-test-teacher@example.test',
        'test-hash',
        'TEACHER'
    )
    RETURNING id INTO teacher_id;

    -- Create a test course.
    INSERT INTO courses (name, code, teacher_id)
    VALUES ('Database Test Course', 'DB-TEST-01', teacher_id)
    RETURNING id INTO course_id;

    -- Test 1: A student can enroll.
    INSERT INTO enrollments (student_id, course_id)
    VALUES (student_id, course_id);

    RAISE NOTICE 'PASS: Student enrollment accepted';

    -- Test 2: BR-03 rejects duplicate enrollments.
    BEGIN
        INSERT INTO enrollments (student_id, course_id)
        VALUES (student_id, course_id);

        RAISE EXCEPTION
            'FAIL: Duplicate enrollment was accepted';
    EXCEPTION
        WHEN unique_violation THEN
            RAISE NOTICE
                'PASS: Duplicate enrollment rejected';
    END;

    -- Test 3: BR-02 rejects teacher enrollments.
    BEGIN
        INSERT INTO enrollments (student_id, course_id)
        VALUES (teacher_id, course_id);

        RAISE EXCEPTION
            'FAIL: Teacher enrollment was accepted';
    EXCEPTION
        WHEN raise_exception THEN
            IF SQLERRM =
                'Enrollment requires a user with STUDENT role'
            THEN
                RAISE NOTICE
                    'PASS: Teacher enrollment rejected';
            ELSE
                RAISE;
            END IF;
    END;

    -- Test 4: An enrolled student cannot become a teacher.
    BEGIN
        UPDATE users
        SET role = 'TEACHER'
        WHERE id = student_id;

        RAISE EXCEPTION
            'FAIL: Enrolled student role change was accepted';
    EXCEPTION
        WHEN raise_exception THEN
            IF SQLERRM =
                'Cannot change role: user has active enrollments'
            THEN
                RAISE NOTICE
                    'PASS: Invalid role change rejected';
            ELSE
                RAISE;
            END IF;
    END;

    RAISE NOTICE 'All enrollment constraint tests passed';
END;
$$;

-- Keep the database unchanged.
ROLLBACK;
