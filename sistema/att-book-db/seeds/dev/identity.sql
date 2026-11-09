-- Development fixtures only.
-- Shared demonstration password: AttendanceDemo123!
-- Apply the database migrations before running this script.

BEGIN;

INSERT INTO users (name, email, password_hash, role)
SELECT
    fixture.name,
    fixture.email,
    '$2b$10$A0golr9Tyav.SbdsFzruT.wbKVQRGcoBOWxyIXl/wv02JK03Q6A4i',
    fixture.role
FROM (
    VALUES
        (
            'Demo Teacher Owner',
            'teacher.owner@example.test',
            'TEACHER'
        ),
        (
            'Demo Other Teacher',
            'teacher.other@example.test',
            'TEACHER'
        ),
        (
            'Demo Enrolled Student',
            'student.enrolled@example.test',
            'STUDENT'
        ),
        (
            'Demo Unenrolled Student',
            'student.unenrolled@example.test',
            'STUDENT'
        )
) AS fixture(name, email, role)
ON CONFLICT (email) DO NOTHING;

-- Reject collisions with incompatible existing accounts.
-- Existing accounts are never silently promoted or reset.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM (
            VALUES
                ('teacher.owner@example.test', 'TEACHER'),
                ('teacher.other@example.test', 'TEACHER'),
                ('student.enrolled@example.test', 'STUDENT'),
                ('student.unenrolled@example.test', 'STUDENT')
        ) AS expected(email, role)
        LEFT JOIN users actual ON actual.email = expected.email
        WHERE actual.id IS NULL
           OR actual.role IS DISTINCT FROM expected.role
           OR actual.password_hash IS DISTINCT FROM
              '$2b$10$A0golr9Tyav.SbdsFzruT.wbKVQRGcoBOWxyIXl/wv02JK03Q6A4i'
    ) THEN
        RAISE EXCEPTION
            'Identity fixtures conflict with existing accounts; review the development database';
    END IF;
END
$$;

COMMIT;