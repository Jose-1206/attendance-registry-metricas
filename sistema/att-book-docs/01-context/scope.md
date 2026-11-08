# 01 — Scope

## In scope

| Capability | Description |
|---|---|
| User registration | Allow users to create an Attendance account. |
| Login | Authenticate registered users and provide protected access. |
| Student and teacher roles | Apply different permissions according to the user's role. |
| Authenticated profile | Allow a signed-in user to retrieve their own profile. |
| Course management | Allow teachers to create and view their courses. |
| Class session management | Allow teachers to create and list sessions for a course. |
| QR generation | Generate a QR code associated with a class session. |
| QR expiration | Use a 15-minute default validity period for generated QR codes. |
| QR validation | Reject invalid or expired QR codes. |
| Attendance registration | Register a student's attendance after the required validations succeed. |
| Duplicate prevention | Prevent duplicate attendance for the same student and class session. |
| Student attendance history | Allow students to review their attendance records. |
| Teacher attendance review | Allow teachers to review attendance records for a class session. |

## Out of scope

| Capability | Reason |
|---|---|
| Biometric attendance | The current Attendance workflow uses QR codes instead of fingerprint or facial recognition. |
| Mandatory GPS validation | Location data is not required for the current attendance registration flow. |
| Offline attendance registration | Attendance validation and persistence depend on communication with the backend. |
| Grade management | Attendance management does not include academic grades. |
| Automatic university-system integration | The current application does not depend on an external institutional enrollment system. |
| Push notifications | Notifications are not required for the current attendance registration workflow. |
| Multi-institution administration | The current scope is focused on the Attendance application for students and teachers. |

## Scope boundary

The MVP focuses on the complete attendance workflow:

**Authentication → Course → Class Session → QR Code → Attendance → History/Review**

Features outside this workflow remain excluded unless the product direction is
updated through the appropriate documentation process.

## Revisiting scope

Changes to the scope must remain consistent with the product vision and the
requirements documented in `03-product` and `04-requirements`.

A capability should only move from "Out of scope" to "In scope" after the
product direction and requirements have been reviewed.

---

**Related:** [`./overview.md`](./overview.md) · [`../03-product/vision.md`](../03-product/vision.md)
