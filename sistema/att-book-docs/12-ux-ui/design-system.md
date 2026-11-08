# 12 — Design System

## Design goals

The Attendance interface is designed for short and repeatable university
attendance tasks on a mobile device.

The interface must make the next valid action clear for each role, keep the QR
check-in flow fast, and provide immediate feedback after operations that change
attendance data.

The design system supports these goals:

- students can register attendance quickly using a QR code;
- teachers can move from a course to a class session and its QR code with few steps;
- role-restricted actions remain clear and consistent;
- success, warning, and error states are understandable without relying only on color;
- shared components are reused across Attendance screens.

## Visual tokens

The following tokens define the visual direction for the Attendance mobile
interface. They are UX/UI design decisions documented for the project and do
not imply that a frontend implementation already exists.

### Colors

| Token | Value | Use |
|---|---|---|
| `--color-primary` | `#2563EB` | Main actions such as Log in, Create course, Create session, Generate QR, and Scan QR. |
| `--color-primary-pressed` | `#1D4ED8` | Pressed state of primary actions. |
| `--color-success` | `#15803D` | Successful account, course, session, QR, or attendance operations. |
| `--color-warning` | `#B45309` | Non-blocking warnings such as QR expiration approaching. |
| `--color-danger` | `#B91C1C` | Invalid QR, expired QR, duplicate attendance, authorization failures, and errors. |
| `--color-text` | `#111827` | Main text. |
| `--color-text-muted` | `#6B7280` | Supporting information and timestamps. |
| `--color-bg` | `#F8FAFC` | Main application background. |
| `--color-surface` | `#FFFFFF` | Cards, forms, dialogs, and navigation surfaces. |
| `--color-border` | `#D1D5DB` | Inputs, cards, and separators. |
| `--color-focus` | `#1D4ED8` | Visible focus state. |

Status information must always combine color with text.

For example, an expired QR code must display **"QR code expired"** instead of
communicating the state only through a red color.

### Typography

| Token | Value | Use |
|---|---|---|
| `--font-size-h1` | `1.75rem` / 700 | Main screen titles. |
| `--font-size-h2` | `1.25rem` / 600 | Section and card-group titles. |
| `--font-size-body` | `1rem` / 400 | Form labels, buttons, records, and general text. |
| `--font-size-caption` | `0.875rem` / 400 | Timestamps, helper text, and QR expiration information. |
| `--line-height-body` | `1.5` | Default text readability. |

### Spacing and shape

| Token | Value | Use |
|---|---|---|
| `--space-1` | `4px` | Tight icon/text spacing. |
| `--space-2` | `8px` | Small internal spacing. |
| `--space-3` | `12px` | Compact form groups. |
| `--space-4` | `16px` | Default screen and card spacing. |
| `--space-6` | `24px` | Section separation. |
| `--space-8` | `32px` | Large screen separation. |
| `--radius-control` | `8px` | Buttons and text fields. |
| `--radius-card` | `12px` | Course, class-session, and attendance cards. |

## Component inventory

| Component | Used by | Attendance behavior |
|---|---|---|
| Primary / secondary button | All interactive screens | One clear primary action per task; disabled during submission. |
| Text field | Register, Login, Courses, Class sessions | Persistent label and inline validation feedback. |
| Password field | Register, Login | Secure entry with accessible show/hide control. |
| Role selector | Register | Allows only `STUDENT` or `TEACHER`. |
| Top app bar | Authenticated screens | Shows title and back action when required. |
| Course card | Courses | Displays course information and opens its class sessions. |
| Class-session card | Class sessions | Displays date, start time, status, QR action, and attendance review action. |
| QR panel | QR code | Displays the class-session QR and expiration information. |
| QR scanner view | Scan QR | Provides camera guidance and scan-processing feedback. |
| Status banner | Scan QR and create flows | Displays explicit success, warning, or error text. |
| Attendance record | History and review | Displays attendance context and check-in time. |
| Empty state | Lists | Explains that no records exist and offers the next valid action. |
| Loading indicator | Backend-connected screens | Shows that a request is being processed. |
| Error banner | Backend-connected screens | Displays readable failure information and Retry when safe. |

## Forms

- Required fields show an inline message when invalid.
- A submit button must not create multiple concurrent requests.
- Backend validation remains authoritative.
- Client-side validation only prevents obvious invalid input.
- Authentication errors must not expose passwords, JWT values, hashes, or
  unnecessary security details.

## QR generation

The QR generation interface follows the Attendance domain and scope.

- A QR code belongs to one `ClassSession`.
- The documented default validity period is **15 minutes**.
- Expiration information must be visible as text.
- An expired QR must no longer be presented as valid.
- A QR is considered successfully generated only after confirmation from the backend.

This behavior supports **BR-05**.

## QR scanning

The scan screen maps real Attendance outcomes to understandable feedback.

| Domain outcome | UI response |
|---|---|
| Valid QR and successful registration | Confirm that attendance was registered and display check-in time when returned. |
| Invalid or expired QR — `BR-05` | Explain that the QR is invalid or expired. |
| Student not enrolled — `BR-06` | Explain that attendance cannot be registered for that course. |
| Duplicate attendance — `BR-07` | Explain that attendance is already registered for the class session. |
| Successful check-in — `BR-08` | Display success only after backend confirmation. |
| Backend/network error | Display a retryable error without claiming attendance was stored. |

## Mobile layout

Attendance is documented as a mobile application.

Screens use a single-column layout by default.

- Content respects device safe areas.
- Forms use consistent horizontal padding.
- Primary actions remain easy to reach.
- Long lists scroll vertically.
- QR codes remain fully visible.
- Text can grow without hiding essential actions.
- Important actions are not communicated only by icons.

## Accessibility target

Target: **WCAG 2.1 AA** principles.

| Aspect | Requirement |
|---|---|
| Contrast | At least 4.5:1 for normal text and 3:1 for large text and essential UI graphics. |
| Touch targets | Interactive controls target approximately 44 × 44 CSS pixels when layout permits. |
| Labels | Every form field and icon-only control has an accessible name. |
| Focus | Keyboard or external-device focus remains visible and logical. |
| Screen readers | Buttons, role selectors, errors, status changes, and scanner state expose meaningful labels. |
| Error handling | Errors identify the affected task and explain what the user can do next. |
| Color independence | Success, warning, expiration, and failure are communicated using text as well as color. |
| Motion | No essential information depends on animation. |

## Usability target

The QR check-in interface supports **NFR-10** from
[`../04-requirements/non-functional.md`](../04-requirements/non-functional.md).

At least 90% of first-time test users should be able to complete the student QR
attendance flow in 30 seconds or less without assistance.

The expected flow is:

1. open Scan QR;
2. point the camera at the teacher's QR code;
3. wait for backend validation;
4. receive clear success or rejection feedback.

---

**Related:** [`./README.md`](./README.md) · [`./scan-qr-screen.md`](./scan-qr-screen.md) · [`../04-requirements/non-functional.md`](../04-requirements/non-functional.md)
