# Attendance History — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Attendance history |
| Serves | [`HU-10 — View my attendance history as a student`](../04-requirements/user-stories.md) |
| Primary users | `STUDENT` |
| Purpose | Display the authenticated student's successfully registered attendance records. |

## Wireframe

```text
+----------------------------------+
| Attendance history               |
|                                  |
| Course: [course name]            |
| Session: [date / start time]     |
| Check-in: [check-in time]        |
| -------------------------------- |
| Course: [course name]            |
| Session: [date / start time]     |
| Check-in: [check-in time]        |
+----------------------------------+
```

Actual values come from attendance records returned for the authenticated
student.

## States

| State | What the user sees |
|---|---|
| Loading | Progress while attendance history is requested. |
| Empty | `You do not have registered attendance yet.` |
| Error | Error banner and Retry. |
| Populated | Attendance records containing class-session context and check-in information. |

## Attendance data

The documented `AttendanceRecord` entity contains:

- student reference;
- session reference;
- check-in time.

The screen presents only data needed by the student to understand their own
attendance history.

## Display rules

- Only the authenticated student's own attendance history is shown.
- Each record identifies the related class-session context.
- Check-in time is shown when returned by the backend.
- The UI does not invent attendance records that do not exist.
- QR tokens, JWT values, passwords, and internal identifiers are not exposed.

## Actions and behavior

- **Retry** repeats the history request after a retryable failure.
- The student can return to the Scan QR flow through the application navigation.
- This screen is read-only and does not modify attendance records.

## Business-rule connection

- **BR-08:** check-in time is recorded when a valid QR code is successfully scanned.

## Accessibility notes

- Records follow a logical reading order.
- Course/session context and check-in time have explicit labels.
- Empty and error states are communicated through text.

---

**Related:** [`./scan-qr-screen.md`](./scan-qr-screen.md) · [`./design-system.md`](./design-system.md) · [`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md)