-- Attendance Registry: development session fixtures.
-- Prerequisites:
--   1. Apply migrations V1, V2 and V3.
--   2. Run seeds/dev/identity.sql.
--   3. Load the courses described in seeds/dev/academic.json.
--
-- This script does not create courses or assume numeric identifiers.
-- Existing fixture sessions are preserved on subsequent runs.

BEGIN;

-- Prevent concurrent executions from inserting duplicate fixtures.
SELECT pg_advisory_xact_lock(20261108, 301);

DO $$
DECLARE
    fixture RECORD;
    resolved_course_id BIGINT;
    matching_courses BIGINT;
    matching_sessions BIGINT;
BEGIN
    FOR fixture IN
        SELECT *
        FROM (
            VALUES
                (
                    'teacher.owner@example.test',
                    'DEMO-FS-01',
                    'DEMO-FS-SESSION-01',
                    'Fullstack demonstration: first class',
                    TIMESTAMP '2026-11-10 08:00:00',
                    TIMESTAMP '2026-11-10 10:00:00'
                ),
                (
                    'teacher.owner@example.test',
                    'DEMO-FS-01',
                    'DEMO-FS-SESSION-02',
                    'Fullstack demonstration: second class',
                    TIMESTAMP '2026-11-11 08:00:00',
                    TIMESTAMP '2026-11-11 10:00:00'
                ),
                (
                    'teacher.owner@example.test',
                    'DEMO-BD-01',
                    'DEMO-BD-SESSION-01',
                    'Database demonstration class',
                    TIMESTAMP '2026-11-10 14:00:00',
                    TIMESTAMP '2026-11-10 16:00:00'
                ),
                (
                    'teacher.other@example.test',
                    'DEMO-IS-01',
                    'DEMO-IS-SESSION-01',
                    'Other teacher ownership verification',
                    TIMESTAMP '2026-11-12 08:00:00',
                    TIMESTAMP '2026-11-12 10:00:00'
                )
        ) AS fixtures(
            teacher_email,
            course_code,
            session_title,
            session_description,
            start_at,
            end_at
        )
    LOOP
        SELECT COUNT(*), MIN(c.id)
        INTO matching_courses, resolved_course_id
        FROM courses c
        JOIN users u ON u.id = c.teacher_id
        WHERE c.code = fixture.course_code
          AND u.email = fixture.teacher_email
          AND u.role = 'TEACHER';

        IF matching_courses <> 1 THEN
            RAISE EXCEPTION
                'Expected one course with code % for teacher %, found %. Load or review academic fixtures first.',
                fixture.course_code,
                fixture.teacher_email,
                matching_courses;
        END IF;

        SELECT COUNT(*)
        INTO matching_sessions
        FROM class_sessions s
        WHERE s.course_id = resolved_course_id
          AND s.title = fixture.session_title;

        IF matching_sessions > 1 THEN
            RAISE EXCEPTION
                'Duplicate session fixtures found: %',
                fixture.session_title;
        END IF;

        IF matching_sessions = 0 THEN
            INSERT INTO class_sessions (
                course_id,
                title,
                description,
                start_at,
                end_at
            )
            VALUES (
                resolved_course_id,
                fixture.session_title,
                fixture.session_description,
                fixture.start_at,
                fixture.end_at
            );
        ELSIF EXISTS (
            SELECT 1
            FROM class_sessions s
            WHERE s.course_id = resolved_course_id
              AND s.title = fixture.session_title
              AND (
                  s.description IS DISTINCT FROM fixture.session_description
                  OR s.start_at IS DISTINCT FROM fixture.start_at
                  OR s.end_at IS DISTINCT FROM fixture.end_at
              )
        ) THEN
            RAISE EXCEPTION
                'Existing session % differs from the fixture. Review it manually; no existing data was overwritten.',
                fixture.session_title;
        END IF;
    END LOOP;
END
$$;

COMMIT;