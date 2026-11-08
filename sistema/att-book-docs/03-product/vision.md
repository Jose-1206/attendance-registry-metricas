# 03 — Problem Framing & Vision

## The problem

University attendance management can become slow and difficult to verify when
it relies on manual roll calls, paper lists, or spreadsheets.

These methods can consume class time, introduce missing or incorrect records,
and make attendance history difficult to consult from a single source.

Attendance Registry addresses this problem through a digital workflow in which
students register attendance using temporary QR codes and teachers manage and
review attendance for their class sessions.

## Who is affected

| Group | How they are affected |
|---|---|
| Students | Wait while attendance is recorded and need a reliable way to confirm their check-ins and review their own attendance history. |
| Teachers | Spend time recording and checking attendance and need a consistent record for each class session. |

These groups match the audiences identified in
[the project overview](../01-context/overview.md).

## Current impact

Manual attendance management can cause:

- Class time spent calling names or checking lists.
- Missing, incorrect, or duplicate attendance records.
- Additional work to verify previous attendance.
- Records distributed across paper lists and spreadsheets.

These are the problems the product aims to address. No measured baseline or
quantified time saving is established in this document.

## Cost of doing nothing

Continuing with manual records can preserve the same verification workload
and difficulties in consulting attendance history.

Teachers may need additional checks to resolve incorrect or missing entries.
Students may continue depending on teacher confirmation to verify their
attendance.

## Success criteria

The proposed workflow must support the following outcomes:

- A teacher can generate a temporary QR code for a class session they manage.
- An authenticated student enrolled in the corresponding course can register
  attendance using a valid QR code.
- The backend validates the student, enrollment, class session, and QR code
  before storing attendance.
- Invalid or expired QR codes are rejected.
- A second attendance registration for the same student and class session is
  rejected.
- A successful registration stores the check-in time.
- Students can review their own attendance history.
- Teachers can review attendance for the class sessions they manage.
- Users cannot access attendance information outside their permitted scope.

These criteria describe expected behavior. Their implementation must be
verified through the corresponding acceptance tests.

## Objectives and proposed metrics

The following metrics are proposed acceptance targets. They are not measured
results or confirmed stakeholder agreements.

| # | Objective | Proposed metric | Verification |
|---|---|---|---|
| 1 | Reduce attendance registration time | A student completes a valid check-in within 30 seconds after the QR code is available and the student is authenticated. | Time the flow during a usability test and record the conditions and result. |
| 2 | Prevent invalid attendance records | All defined acceptance cases for invalid QR codes, expired QR codes, and duplicate check-ins are rejected. | Execute the corresponding acceptance tests and record their results. |
| 3 | Give students visibility into their attendance | Every successful check-in appears in the student's history after refreshing the view. | Register attendance and retrieve the authenticated student's history. |
| 4 | Give teachers a reliable session attendance list | The session attendance list includes every successful check-in from the test scenario and excludes rejected attempts. | Compare the teacher's session list with the expected records. |
| 5 | Support the main workflow through the web portal | Both role scenarios can be completed through the portal without manually editing database records. | Execute the teacher scenario for course and session management, QR generation, and attendance review; execute the student scenario for QR check-in and history review. |
| 6 | Protect access to attendance information | All defined acceptance cases for unauthenticated requests, forbidden roles, and access outside the user's permitted scope are rejected. | Execute authentication, role, and ownership authorization tests. |

## Agreement and validation status

Team or stakeholder agreement on these targets remains pending confirmation.

Before closing the product section, record who approved the targets, when they
were approved, and a reference to the agreement in a pull request or meeting
record.

If a target changes, update this document and the affected acceptance criteria.
Report test results separately from proposed targets.

## Traceability

| Source | Purpose |
|---|---|
| [Project overview](../01-context/overview.md) | Identifies users, capabilities, and workflow constraints. |
| [Scope](../01-context/scope.md) | Defines product boundaries. |
| [User stories](../04-requirements/user-stories.md) | Describes student and teacher needs and acceptance criteria. |
| [Domain rules](../02-domain/entities-and-rules.md) | Defines attendance validation and record management constraints. |

## Documentation alignment

This vision describes the Fullstack web portal.

The project overview reviewed during this correction still describes a mobile
application. Its alignment with the Fullstack delivery remains pending with
the owner of that document.

Review this document when the product scope, user needs, or attendance workflow
changes.

Proposed changes to affected groups or success criteria must be reviewed with
the team and reflected in the related documentation.

---

**Related:** [Project overview](../01-context/overview.md) ·
[Scope](../01-context/scope.md) ·
[User stories](../04-requirements/user-stories.md) ·
[Definition of Ready](../00-governance/definition-of-ready.md) ·
[Definition of Done](../00-governance/definition-of-done.md)