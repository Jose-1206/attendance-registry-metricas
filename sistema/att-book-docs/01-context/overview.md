# 01 — Overview

## What it does

Attendance Registry is a web application for university attendance control.

Teachers can manage their courses, create class sessions, generate temporary
QR codes, and review attendance records for their class sessions.

Students can authenticate, scan a QR code provided during a class, register
their attendance, and review their own attendance history.

## Main capabilities

| Capability | Description |
|---|---|
| Authentication | Users can register and sign in using their credentials. |
| Role-based access | The application distinguishes between `STUDENT` and `TEACHER` roles. |
| Authenticated profile | An authenticated user can retrieve their own profile information. |
| Course management | Teachers can create and view the courses they manage. |
| Class sessions | Teachers can create and list class sessions associated with a course. |
| QR generation | A temporary QR code can be generated for a class session. |
| QR validation | The system validates the QR code and rejects invalid or expired codes. |
| Attendance registration | A student can register attendance by scanning a valid QR code. |
| Duplicate prevention | The same student cannot register attendance twice for the same class session. |
| Student attendance history | Students can review their previously registered attendance records. |
| Teacher attendance review | Teachers can review attendance records associated with a class session. |

## Who it is for

| Audience | Uses it to |
|---|---|
| Students | Register attendance and review their own attendance history. |
| Teachers | Manage courses and class sessions, generate QR codes, and review attendance. |

## Problem addressed

Manual attendance through roll calls, paper lists, or spreadsheets can consume
class time and make attendance information harder to verify.

Attendance provides a digital workflow based on authenticated users and
time-limited QR codes so that attendance can be registered and reviewed from
the application.

## Main workflow

1. A user authenticates in the application.
2. A teacher manages a course.
3. Students are associated with the corresponding course.
4. The teacher creates a class session.
5. The system generates a temporary QR code for the session.
6. A student scans the QR code.
7. The system validates the student, QR code, class session, and enrollment.
8. The system creates the attendance record with the check-in time.
9. The student can review the attendance in their history.
10. The teacher can review attendance for the class session.

## Constraints

| Constraint | Description |
|---|---|
| Authentication | Protected attendance operations require an authenticated user. |
| Roles | Student and teacher actions are separated by authorization rules. |
| QR expiration | QR codes have a limited validity period; the current default is 15 minutes. |
| Duplicate prevention | A student can register only one attendance record for the same class session. |
| Server validation | Attendance is stored only after the relevant validations succeed. |

---

**Related:** [`./scope.md`](./scope.md) · [`./glossary.md`](./glossary.md)
