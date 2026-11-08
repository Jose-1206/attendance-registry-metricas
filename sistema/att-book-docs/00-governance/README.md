# 00 — Governance

> [!NOTE]
> This section remains under review. Remove this notice after the pending
> criteria below have been verified.

## Purpose

This section defines the working agreements for Attendance Registry:
how tasks become ready, how completed work is verified, how Git changes are
reviewed, how documentation is maintained, and how sensitive information is
protected.

## Documents

| Document | Answers | Priority |
|---|---|---|
| [definition-of-ready.md](./definition-of-ready.md) | When can work begin? | ⭐ |
| [definition-of-done.md](./definition-of-done.md) | When is work complete? | ⭐ |
| [git-conventions.md](./git-conventions.md) | How are branches, commits, and pull requests managed? | ⭐ |
| [documentation-rules.md](./documentation-rules.md) | When is documentation complete? | ⭐ |
| [agile-conventions.md](./agile-conventions.md) | How is project work organized? | — |
| [security-policy.md](./security-policy.md) | How are secrets, dependencies, and access protected? | — |
| [_template-sprint-retro.md](./_template-sprint-retro.md) | Reusable sprint retrospective template. | — |

## Scope

These agreements govern the Attendance Registry project and its documentation.

They do not define independently released modules or distributed-system
coordination.

## Review status

- [x] The Definition of Ready contains project-specific readiness criteria.
- [x] The Definition of Done contains project-specific completion criteria.
- [x] Documentation rules define the document lifecycle and review process.
- [x] The security policy identifies the Attendance development team as owner.
- [ ] The security policy corrections proposed in PR #85 are integrated.
- [ ] Agile conventions are complete and their Markdown renders correctly.
- [ ] Git conventions are complete and their Markdown renders correctly.
- [ ] References to Ready and Done have been verified from 01-context onward.
- [ ] All completed documents have been reviewed for unresolved instructions and placeholders.

## Pending findings

The versions supplied during this review ended inside code blocks:

- agile-conventions.md ended after the Done workflow state.
- git-conventions.md ended after the documentation branch naming example.

Confirm the complete source of each file, close any unclosed code blocks,
and complete the working agreements with the responsible contributor.
Do not invent meeting schedules, approval rules, or branching policies.

The product README changes proposed in PR #88 add links to Ready and Done.
References in the other sections still require verification. Any missing link
in a file assigned to another contributor must be coordinated with that owner.

## Preserved instructions and templates

The INSTRUCTIONS block in documentation-rules.md is intentionally preserved.

Reusable files beginning with _template- may retain instructions and
placeholders. They are completed when copied for a specific work cycle.

## Applying Ready and Done

Use the [Definition of Ready](./definition-of-ready.md) before starting work
and the [Definition of Done](./definition-of-done.md) when reviewing completion.

Follow the [documentation rules](./documentation-rules.md) when creating or
updating repository documents.

---

**Related:** [SDD guide](../00-sdd-guide.md) ·
[Documentation rules](./documentation-rules.md)
