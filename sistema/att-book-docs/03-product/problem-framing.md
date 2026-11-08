# 03 — Problem Framing

## The problem

University attendance management can become slow and difficult to verify when it is recorded through manual roll calls, paper lists, or spreadsheets.

These methods consume class time, may introduce missing or incorrect attendance records, and make it difficult for students and teachers to access a reliable attendance history from a single source.

The Attendance application addresses this problem by providing a digital process where students register their attendance through QR codes and teachers can manage and review attendance information.

## Who is affected

| Group | How they are affected |
|---|---|
| Students | Spend class time waiting for attendance to be taken and may not have an immediate way to verify whether their attendance was recorded correctly. |
| Teachers | Must spend time recording, checking, and reviewing attendance instead of focusing on teaching activities. |
| Students reviewing their history | Need a clear record of previous check-ins without depending on paper lists or manual verification from teachers. |
| Teachers reviewing a class session | Need a consistent and traceable list of attendance records for each class session. |

## Current impact

The current attendance process creates several operational difficulties:

- Class time can be reduced because teachers need to manually call names or review attendance lists.
- Attendance information may contain errors, duplicated records, or missing entries.
- Students and teachers depend on manual verification when they need to review previous attendance information.
- Attendance records are distributed across different sources instead of being available in one reliable system.

## Cost of doing nothing

If attendance continues to depend on manual records, classes may continue losing time during attendance verification.

Incorrect, duplicated, or missing entries may require additional manual checking, increasing the workload for teachers and reducing confidence in attendance information.

Students may also continue depending on external records or teacher confirmation to verify their attendance history.

## Success criterion

The problem is considered solved when:

- A teacher can create a valid QR code for a class session.
- An authenticated student can register attendance by scanning the QR code within the allowed time.
- The system securely stores the attendance record with the check-in time.
- Invalid or duplicate attendance attempts are rejected.
- Students can review their attendance history.
- Teachers can review attendance records associated with their class sessions.

## Traceability

The problem framing is supported by the following project documentation:

| Source | Purpose |
|---|---|
| [`vision.md`](./vision.md) | Defines the product goals, users, and expected outcomes of the Attendance application. |
| [`../01-context/overview.md`](../01-context/overview.md) | Defines the general context and purpose of the Attendance application. |
| User stories | Define the needs of students and teachers related to attendance registration, QR scanning, and attendance history. |
| Domain rules | Define the business constraints related to attendance validation and record management. |

## Reviewing the problem

The problem statement should be reviewed when there are relevant changes in the product scope, user needs, or attendance workflow.

Any new affected group or success criterion should be supported by stakeholder validation and linked documentation before modifying the agreed product direction.

---

**Related:** [`./vision.md`](./vision.md) · [`../01-context/scope.md`](../01-context/scope.md)
