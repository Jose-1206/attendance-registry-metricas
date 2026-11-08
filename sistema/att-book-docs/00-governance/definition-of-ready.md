# 00 — Definition of Ready

A story or task is considered Ready when the Attendance development team has
enough information to begin working without inventing requirements, business
rules, or expected behavior.

## Ready checklist

| # | Criterion | Verification |
|---|---|---|
| 1 | The objective is clear | The expected result of the story or task is clearly described |
| 2 | The affected user or actor is identified | The story identifies whether it applies to a student, teacher, or another system actor |
| 3 | Acceptance criteria are defined | The expected behavior can be verified after implementation |
| 4 | Relevant business rules are known | Rules required by the story are documented or referenced |
| 5 | Dependencies are identified | Related stories, requirements, modules, or technical dependencies are known |
| 6 | The affected project area is identified | The story indicates which functionality, API, data, screen, or document is affected |
| 7 | Required data is understood | The information needed by the feature is identified when applicable |
| 8 | Access restrictions are clear | The permitted user role is identified when the functionality is protected |
| 9 | UI behavior is clear when applicable | A wireframe, mockup, or interaction description exists when a screen changes |
| 10 | No blocking question remains unresolved | Work can begin without making an unapproved project decision |

## Attendance-specific considerations

When a story affects QR-based attendance registration, the applicable validation
rules must be understood before implementation.

Relevant cases may include:

- valid QR codes;
- invalid QR codes;
- expired QR codes;
- duplicate attendance attempts;
- authenticated student access;
- teacher-restricted operations.

Only the cases affected by the story need to be defined.

## What Ready means

Ready does not mean that every technical implementation detail must already be
decided.

It means that the team understands what must be built, why it is needed, and how
the expected result can be verified.

---

**Related:** [`definition-of-done.md`](./definition-of-done.md) · [`README.md`](./README.md)
