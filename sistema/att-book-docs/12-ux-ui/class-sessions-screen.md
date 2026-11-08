# Class Sessions — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Class sessions |
| Serves | [`HU-06 — Create a class session`](../04-requirements/user-stories.md), [`HU-07 — View class sessions for a course`](../04-requirements/user-stories.md) |
| Primary users | `TEACHER` |
| Purpose | Display the class sessions belonging to a selected course and allow a teacher to create a session. |

## Wireframe

```text
+----------------------------------+
| < Courses   Class sessions   [+] |
| Course: [selected course]        |
|                                  |
| Date: [session date]             |
| Start: [start time]              |
| Status: [session status]         |
| [Generate QR]                    |
| [View attendance]                |
| -------------------------------- |
| Date: [session date]             |
| Start: [start time]              |
| Status: [session status]         |
| [View attendance]                |
|                                  |
| [+ Create class session]         |
+----------------------------------+
```

The actual values come from the selected course and its `ClassSession` records.

## States

| State | What the user sees |
|---|---|
| Loading | Progress while sessions for the selected course are requested. |
| Empty | `No class sessions have been created for this course.` plus Create class session. |
| Error | Error banner and Retry. |
| Populated | Session cards containing date, start time, status, and available teacher actions. |
| Creating | Session form with submission progress. |
| Create success | Confirmation and the new session in the list. |

## Class-session data

The documented `ClassSession` entity contains:

- course reference;
- date;
- start time;
- status.

Every class session belongs to exactly one existing course.

## Validation

| Field | Rule | Message shown |
|---|---|---|
| Date | Required | `Choose the class date.` |
| Start time | Required | `Choose the start time.` |

The session status must follow the lifecycle supported by the backend/domain.

## Actions and behavior

- A class session is always created inside an existing selected course.
- **Generate QR** opens the QR screen for the selected session.
- **View attendance** opens the teacher attendance-review screen.
- Students cannot create or manage class sessions.

## Business-rule connection

- **BR-04:** a class session can only be created for an existing course.

## Accessibility notes

- Every session card exposes its date, start time, and status.
- Buttons identify both the action and the relevant session context.
- Session status is communicated with text rather than color only.

---

**Related:** [`./courses-screen.md`](./courses-screen.md) · [`./qr-code-screen.md`](./qr-code-screen.md) · [`./attendance-review-screen.md`](./attendance-review-screen.md)