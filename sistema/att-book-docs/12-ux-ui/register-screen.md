# Register — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Register |
| Serves | [`HU-01 — Register a user account`](../04-requirements/user-stories.md) |
| Primary users | New students and teachers |
| Purpose | Create an Attendance account using one of the supported roles. |

## Wireframe

```text
+----------------------------------+
| Attendance                       |
| Create account                   |
|                                  |
| Name                             |
| [____________________________]   |
| Email                            |
| [____________________________]   |
| Password                         |
| [__________________________][👁] |
| Role                             |
| ( ) Student   ( ) Teacher        |
|                                  |
| [        Create account       ]  |
|                                  |
| Already have an account? Log in  |
+----------------------------------+
```

## States

| State | What the user sees |
|---|---|
| Ready | Registration form with Name, Email, Password, and Role. |
| Submitting | Create account action disabled with progress feedback. |
| Validation error | Inline messages for fields that require correction. |
| Server error | Human-readable error returned after the registration request fails. |
| Success | Confirmation that the account was created and navigation to the next valid authentication step. |

## Validation rules

| Field | Rule | Message shown |
|---|---|---|
| Name | Required | `Enter your name.` |
| Email | Required and valid email format | `Enter a valid email address.` |
| Password | Required | `Enter a password.` |
| Role | Required; only `STUDENT` or `TEACHER` | `Choose Student or Teacher.` |

The backend remains authoritative for email uniqueness and any additional
registration validation.

## Actions and behavior

- **Create account** sends one registration request.
- **Log in** opens the Login screen.
- The role selector exposes only **Student** and **Teacher**.
- User-facing labels use Student and Teacher while domain values remain
  `STUDENT` and `TEACHER`.
- Passwords are never displayed after registration.

## Accessibility notes

- Every input has a persistent accessible label.
- Validation messages identify the affected field.
- The role selector exposes the selected state.
- Password visibility uses an accessible label such as **Show password** or
  **Hide password**.

---

**Related:** [`./login-screen.md`](./login-screen.md) · [`./design-system.md`](./design-system.md) · [`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md)