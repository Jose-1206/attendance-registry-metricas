# Attendance Review — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Class attendance review |
| Serves | [`HU-11 — Review attendance for a class session as a teacher`](../04-requirements/user-stories.md) |
| Primary users | `TEACHER` |
| Purpose | Allow a teacher to review attendance records associated with a selected class session. |

## Wireframe

```text
+----------------------------------+
| < Class sessions   Attendance    |
| Course: [selected course]        |
| Session: [date / start time]     |
|                                  |
| Registered attendance            |
|                                  |
| Student: [student]               |
| Check-in: [check-in time]        |
| -------------------------------- |
| Student: [student]               |
| Check-in: [check-in time]        |
+----------------------------------+
```

Actual values come from attendance records returned for the selected class
session.

## States

| State | What the user sees |
|---|---|
| Loading | Progress while attendance records are requested. |
| Empty | `No attendance has been registered for this class session.` |
| Error | Error banner and Retry. |
| Populated | Attendance records associated with the selected class session. |

## Display rules

- The selected course and class-session context remains visible.
- Each returned attendance record displays the student information allowed by
  the API and the registered check-in time.
- The list represents stored attendance records.
- The UI must not mark a student as **absent** unless the backend provides the
  complete enrollment and attendance data required to support that conclusion.
- QR tokens, passwords, JWTs, and unrelated profile information are not shown.

## Actions and behavior

- **Retry** repeats the request for the selected class session.
- Back navigation returns to Class sessions.
- Reviewing the screen does not modify attendance records.
- This screen does not create attendance on behalf of a student.

## Business-rule connection

- **BR-08:** every successfully created attendance record stores the student's
  check-in time.

## Accessibility notes

- Each attendance record has a clear student label and check-in label.
- The selected class-session context is announced before the list.
- Empty and error states are communicated through text rather than color alone.

---

**Related:** [`./class-sessions-screen.md`](./class-sessions-screen.md) · [`./design-system.md`](./design-system.md) · [`../04-requirements/user-stories.md`](../04-requirements/user-stories.md)