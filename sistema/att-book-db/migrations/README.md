# Database Migrations

## Overview

This directory contains the PostgreSQL database migrations for Attendance Registry.

The migrations define the initial identity and academic tables, including course enrollments and their integrity constraints.

## Migration Order

Execute the SQL files in the following order:

1. `V1__create_users_and_courses.sql`
   - Creates the `users` table.
   - Creates the `courses` table.
   - Adds the teacher foreign key and supporting index.

2. `V2__create_enrollments.sql`
   - Creates the `enrollments` table.
   - Adds foreign keys to users and courses.
   - Prevents duplicate course enrollments.
   - Enforces student-only enrollment through PostgreSQL triggers.
   - Prevents changing the role of an enrolled student.

## Business Rules

**BR-02 — Student-only enrollment**

Only users with the `STUDENT` role can enroll in courses.

**BR-03 — Unique course enrollment**

A student cannot have more than one enrollment in the same course.

## Local Validation

Tested with PostgreSQL 17 using a temporary Docker container.

Both migrations executed successfully.

The following test cases passed:

- Valid student enrollment.
- Duplicate enrollment rejection.
- Teacher enrollment rejection.
- Enrolled student role-change rejection.

Test script: `tests/enrollment_constraints_test.sql`

The test script uses a transaction and rolls back its test data.

## Integration Notes

The API currently uses Hibernate schema validation (`ddl-auto=validate`), so database tables must exist before the API starts.

The infrastructure integration must provide:

- A running PostgreSQL database.
- Database connection environment variables.
- Execution of V1 before V2 on a fresh database.
- A documented migration execution strategy.

These SQL files have been tested manually. Automatic migration execution is not yet configured.

Do not execute these CREATE TABLE migrations again against a database where they have already been applied.
