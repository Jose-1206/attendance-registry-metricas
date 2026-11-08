# Profile — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Profile |
| Serves | [`HU-03 — View my authenticated profile`](../04-requirements/user-stories.md) |
| Primary users | Authenticated students and teachers |
| Purpose | Display the authenticated user's own account information. |

## Wireframe

```text
+----------------------------------+
| Profile                          |
|                                  |
| Name                             |
| [authenticated user name]        |
|                                  |
| Email                            |
| [authenticated user email]       |
|                                  |
| Role                             |
| Student / Teacher                |
|                                  |
| [            Log out          ]  |
+----------------------------------+
```

The values displayed by the real application come from the authenticated
user's backend profile.

## States

| State | What the user sees |
|---|---|
| Loading | Progress while profile data is requested. |
| Error | Error banner and Retry action. |
| Populated | Name, email, and role of the authenticated user. |
| Session invalid/expired | Authentication message followed by navigation to Login. |

## Display rules

- Only the authenticated user's own profile is displayed.
- Role is shown using the user-facing value **Student** or **Teacher**.
- Passwords are never displayed.
- JWTs, password hashes, and internal security values are never displayed.

## Actions and behavior

- **Retry** repeats the profile request.
- **Log out** clears the local authenticated session and returns to Login.

## Accessibility notes

- Every displayed value has an explicit label.
- Role and authentication errors are readable by assistive technology.
- Log out uses a visible text label and is not represented only by an icon.

---

**Related:** [`./login-screen.md`](./login-screen.md) · [`./design-system.md`](./design-system.md) · [`../00-governance/security-policy.md`](../00-governance/security-policy.md)