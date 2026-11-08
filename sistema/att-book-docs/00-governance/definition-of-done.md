# 00 — Definition of Done

A story or task is considered Done when its expected behavior has been
implemented, verified, and documented when necessary.

A change is not considered Done only because the code compiles or a file has
been modified.

## Done checklist

| # | Criterion | Verification |
|---|---|---|
| 1 | The expected behavior is implemented | The result matches the story, requirement, or task |
| 2 | Acceptance criteria are satisfied | Every applicable acceptance criterion has been checked |
| 3 | The affected functionality works correctly | The main flow is executed successfully |
| 4 | Relevant business rules remain valid | Rules affected by the change are verified |
| 5 | Authentication and authorization remain correct | Protected functionality is checked with the appropriate user role when applicable |
| 6 | Persistence works correctly when affected | Required data can be stored and retrieved correctly |
| 7 | No sensitive information is exposed | Changed files contain no passwords, tokens, secrets, or private environment values |
| 8 | Related documentation is updated | Documentation affected by the change is updated |
| 9 | The pull request contains related changes only | The changed files are reviewed before integration |
| 10 | No known blocking defect remains | Problems that prevent the expected behavior from working are resolved |

## Attendance critical flows

When affected by a change, the following functionality must remain valid:

- user registration and authentication;
- student and teacher access restrictions;
- course management;
- class session management;
- QR generation;
- QR validation and expiration;
- attendance registration;
- duplicate attendance prevention;
- student attendance history;
- teacher attendance review.

Only functionality affected by the current change must be revalidated.

## Testing

Automated tests should be added or updated when the affected functionality is
covered by automated testing.

This document does not define a mandatory numerical test coverage threshold
unless the project actually measures and enforces one.

---

**Related:** [`definition-of-ready.md`](./definition-of-ready.md) · [`README.md`](./README.md)
