# 04 — Requirements

## Purpose

This section defines what the Attendance application must do from the user's
point of view and the quality levels it must meet.

The requirements are connected to the Attendance domain, modules, business
rules, and verification activities through explicit traceability.

## Documents

| Document | Answers | Priority |
|---|---|---|
| [user-stories.md](./user-stories.md) | What must the system do from the user's point of view? | ⭐ |
| [non-functional.md](./non-functional.md) | What measurable quality requirements must the system meet? | ⭐ |
| [traceability-matrix.md](./traceability-matrix.md) | How are requirements connected to modules, design documents, and verification? | ⭐ |
| [_template-hu.md](./_template-hu.md) | Template used when creating an individual user story. | — |

## Requirement sources

The requirements are derived from the Attendance product vision, problem
framing, domain rules, and documented user needs.

| Source | Purpose |
|---|---|
| [`../03-product/vision.md`](../03-product/vision.md) | Defines product objectives and expected outcomes. |
| [`../03-product/problem-framing.md`](../03-product/problem-framing.md) | Defines the problem and affected users. |
| [`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md) | Defines domain entities and business rules. |
| [`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md) | Defines the modules responsible for each business area. |

## Requirement principles

- Each user story has a unique `HU-NN` identifier.
- Each user story belongs to one Attendance module.
- Acceptance criteria must describe observable behaviour.
- Business rules are referenced by their `BR-NN` identifiers.
- Non-functional requirements use measurable thresholds.
- Requirements should remain traceable to product, domain, architecture, and verification documentation.

## Ready when

- [x] `user-stories.md` contains the current Attendance user stories.
- [x] `non-functional.md` contains measurable requirements.
- [x] `traceability-matrix.md` maps the documented user stories and NFRs.
- [x] Every `HU-NN` identifies one module.
- [x] Every `NFR-NN` contains a measurable threshold and measurement method.

---

**Related:** [`../03-product/README.md`](../03-product/README.md) · [`../05-architecture/README.md`](../05-architecture/README.md)
