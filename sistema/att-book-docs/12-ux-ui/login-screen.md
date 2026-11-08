# Login — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Login |
| Serves | [`HU-02 — Log in to Attendance`](../04-requirements/user-stories.md) |
| Primary users | Registered students and teachers |
| Purpose | Authenticate an Attendance user and provide access according to their role. |

## Wireframe

```text
+----------------------------------+
| Attendance                       |
| Log in                           |
|                                  |
| Email                            |
| [____________________________]   |
| Password                         |
| [__________________________][👁] |
|                                  |
| [            Log in           ]  |
|                                  |
| New to Attendance?               |
| Create account                   |
+----------------------------------+
```

## States

| State | What the user sees |
|---|---|
| Ready | Email and Password fields with the Log in action. |
| Submitting | Log in action disabled with progress feedback. |
| Validation error | Inline feedback for missing or malformed input. |
| Authentication error | Message explaining that authentication was not successful. |
| Success | Navigation to the authenticated area according to the returned role. |

## Validation rules

| Field | Rule | Message shown |
|---|---|---|
| Email | Required and valid email format | `Enter a valid email address.` |
| Password | Required | `Enter your password.` |

## Role-based navigation

| Role | Main destinations |
|---|---|
| `STUDENT` | Scan QR, Attendance history, Profile |
| `TEACHER` | Courses, Profile |

UI visibility does not replace backend authorization.

Protected operations continue to depend on JWT authentication and the
authorization rules enforced by the backend.

## Security behavior

- Password values are never displayed after submission.
- JWT values are never shown in the UI.
- Authentication failures do not expose internal security information.
- An invalid or expired authenticated session returns the user to Login.

## Accessibility notes

- Input labels remain visible while typing.
- Authentication errors are announced as error text.
- Password visibility has an accessible label.
- The primary action remains reachable through keyboard or accessibility input.

---

**Related:** [`./register-screen.md`](./register-screen.md) · [`./profile-screen.md`](./profile-screen.md) · [`./design-system.md`](./design-system.md)