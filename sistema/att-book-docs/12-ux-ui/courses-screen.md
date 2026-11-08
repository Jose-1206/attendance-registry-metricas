# Courses — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Courses |
| Serves | [`HU-04 — Create a course`](../04-requirements/user-stories.md), [`HU-05 — View my courses as a teacher`](../04-requirements/user-stories.md) |
| Primary users | `TEACHER` |
| Purpose | Allow a teacher to create courses and view the courses associated with their account. |

## Wireframe

```text
+----------------------------------+
| Courses                      [+] |
|                                  |
| Course name                      |
| Course code                      |
| > View class sessions            |
| -------------------------------- |
| Course name                      |
| Course code                      |
| > View class sessions            |
|                                  |
| [+ Create course]                |
+----------------------------------+
```

Actual course names and codes come from the backend.

## States

| State | What the user sees |
|---|---|
| Loading | Progress while the teacher's courses are requested. |
| Empty | `You do not have courses yet.` plus Create course. |
| Error | Error banner and Retry. |
| Populated | Course cards with course name, code, and access to class sessions. |
| Creating | Course form with submission progress. |
| Create success | Confirmation and the newly created course in the list. |

## Create-course fields

The `Course` domain entity defines:

- `name`;
- `code`;
- teacher reference.

The teacher reference comes from the authenticated teacher and is not manually
selected from this screen.

## Validation

| Field | Rule | Message shown |
|---|---|---|
| Course name | Required | `Enter the course name.` |
| Course code | Required | `Enter the course code.` |

Additional domain or backend validation remains authoritative.

## Actions and behavior

- **Create course** is available only to `TEACHER`.
- Selecting a course opens its Class sessions screen.
- The application does not expose course-management actions to `STUDENT`.
- Course ownership follows the authenticated teacher.

## Business-rule connection

- **BR-01:** only users with the `TEACHER` role can create and manage courses.

## Accessibility notes

- Each course card has an accessible label containing its course information.
- Create and navigation actions use text labels.
- Empty and error states are communicated using text rather than color alone.

---

**Related:** [`./class-sessions-screen.md`](./class-sessions-screen.md) · [`./design-system.md`](./design-system.md) · [`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md)