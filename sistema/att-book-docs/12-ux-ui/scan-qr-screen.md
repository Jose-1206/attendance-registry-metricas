# Scan QR — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Scan QR |
| Serves | [`HU-09 — Record attendance by scanning a QR code`](../04-requirements/user-stories.md) |
| Primary users | `STUDENT` |
| Purpose | Scan a class QR code, send it for backend validation, and display the attendance result. |

## Wireframe

```text
+----------------------------------+
| Scan QR                          |
|                                  |
| Point the camera at the QR code  |
| generated for the class session. |
|                                  |
| +------------------------------+ |
| |                              | |
| |        CAMERA / QR AREA      | |
| |                              | |
| +------------------------------+ |
|                                  |
| Keep the QR inside the frame.    |
|                                  |
| Status: Ready to scan            |
+----------------------------------+
```

## States

| State | What the user sees |
|---|---|
| Ready | Scanner area and short instructions. |
| Permission required | Message explaining that camera access is required. |
| Scanning | Scanner is actively looking for a QR code. |
| Processing | Scanning pauses while the backend validates the QR and attendance rules. |
| Success | `Attendance registered.` plus check-in time when returned. |
| Invalid/expired QR | Message explaining that the QR is invalid or expired. |
| Not enrolled | Message explaining that the student is not enrolled in the course. |
| Duplicate | Message explaining that attendance is already registered for the class session. |
| Network/backend error | Message explaining that attendance could not be confirmed and Retry when appropriate. |

## Domain outcomes

| Rule | UI result |
|---|---|
| `BR-05` | `This QR code is invalid or has expired.` |
| `BR-06` | `Attendance cannot be registered because you are not enrolled in this course.` |
| `BR-07` | `Your attendance for this class session is already registered.` |
| `BR-08` | `Attendance registered.` plus the check-in time when available. |

The application must preserve the outcome returned by the backend.

An invalid, expired, duplicate, unauthorized, or failed request must never be
converted into a successful attendance state.

## Actions and behavior

- QR scanning is available to `STUDENT`.
- After detecting one QR code, the scanner pauses while the registration request
  is processed.
- This prevents multiple concurrent attendance submissions.
- After success, the user can return to the main student area or open Attendance history.
- Retry is available only when repeating the operation is safe.

## Usability target

This screen is the principal interface measured by **NFR-10**.

The normal flow requires no manual attendance data entry:

1. open Scan QR;
2. point the camera at the class QR;
3. wait for backend validation;
4. receive immediate feedback.

At least 90% of first-time test users should complete this flow in 30 seconds or
less without assistance.

## Accessibility notes

- Scanner instructions are always available as text.
- Camera content is complemented by explicit scanner status.
- Success and failures use text in addition to color.
- Status changes are announced to assistive technology.

---

**Related:** [`./attendance-history-screen.md`](./attendance-history-screen.md) · [`./design-system.md`](./design-system.md) · [`../04-requirements/non-functional.md`](../04-requirements/non-functional.md)