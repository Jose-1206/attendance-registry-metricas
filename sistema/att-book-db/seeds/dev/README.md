# Academic Demonstration Data

## Status

`academic.json` defines demonstration scenarios.
It does not create users, create tables or insert database records.

Database loading remains pending the final migrations and identity seed.

## Dataset

- Two teacher references.
- Two student references.
- Three courses.
- Two enrollments for one student.
- One student without enrollments.

## Integration Requirements

Before loading the dataset:

1. Apply the agreed database migrations.
2. Create demonstration users through the identity seed.
3. Map each required user reference to an existing user identifier.
4. Verify that each mapped user has the expected role.
5. Load courses before enrollments.

The loader must use database-generated identifiers, run transactionally
and avoid duplicate records when executed again.

Do not create a duplicate enrollment as seed data. Test the duplicate
enrollment rule through a separate negative test.

## Intended Scenarios

- A teacher manages their own courses.
- A teacher cannot manage another teacher's course.
- A student is enrolled in two courses.
- Another student remains outside those courses.
- Duplicate enrollment attempts are rejected.

Attendance scenarios additionally require class sessions and QR fixtures.

## Format Validation

Run from the repository root:

    python3 -m json.tool seeds/dev/academic.json

This validates JSON syntax only. Database loading and integrity checks
remain pending integration.
