# 01 — Glossary

## Terms

| Term | Definition | Forbidden synonyms |
|---|---|---|
| User | An authenticated person with an Attendance account and an application role. | Account holder |
| Student | A user with the `STUDENT` role who can register attendance and review their own attendance history. | Learner, pupil |
| Teacher | A user with the `TEACHER` role who can manage courses, class sessions, QR codes, and attendance review. | Professor, instructor |
| Role | The authorization category assigned to a user: `STUDENT` or `TEACHER`. | User type |
| Course | A university course managed by a teacher and associated with class sessions. | Subject |
| Enrollment | The relationship that associates a student with a course. | Registration |
| Class Session | A specific occurrence of a course for which attendance can be registered. | Class, attendance session |
| QR Code | A temporary code associated with one class session and used to request an attendance check-in. | Barcode, permanent code |
| QR Expiration | The time after which a QR code can no longer be used to register attendance. | Timeout |
| Check-in | A successful attendance registration after the required validations succeed. | Scan |
| Attendance Record | A persisted record linking a student to a class session and storing the check-in time. | Presence mark |
| Attendance History | The attendance records available to a student for review. | Log |
| Attendance Review | The teacher's view of attendance records associated with a class session. | Student history |
| Authentication | Verification of a user's credentials before protected operations are allowed. | Authorization |
| Authorization | Verification that an authenticated user has permission to perform an action. | Authentication |
| JWT | The signed token used by the backend to authenticate protected requests. | Session id |
| Module | A bounded context inside the Attendance backend, with its own responsibility, data ownership, and public API. | Service |

## Why terminology matters

The same domain term should be used consistently across context, product,
requirements, domain, architecture, API, and testing documentation.

This avoids different documents describing the same Attendance concept with
different names.

## Adding a term

Add a term when it is used by multiple documents and could reasonably be
interpreted in more than one way.

Implementation-specific terms should remain in the document where their
technical meaning is required.

---

**Related:** [`./overview.md`](./overview.md) · [`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md)
