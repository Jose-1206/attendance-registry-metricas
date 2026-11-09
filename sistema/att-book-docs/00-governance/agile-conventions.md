# 00 — Agile Conventions

## Purpose

These working agreements organize Attendance Registry tasks and keep progress,
dependencies, and review decisions visible.

A fixed sprint duration, meeting schedule, or estimation method applies only
when explicitly agreed by the team.

## Workflow

Tasks move through these states:

```text
Backlog → Ready → In Progress → In Review → Done
```

| State | Meaning |
|---|---|
| Backlog | The task is identified but may still require clarification. |
| Ready | The objective, owner, acceptance criteria, and dependencies are clear. |
| In Progress | The assigned contributor is working on the task. |
| In Review | A pull request is available for review with validation results. |
| Done | Acceptance criteria are met, required validation passes, and the reviewed change is integrated. |

A commit or an open pull request alone does not make a task Done.

## Task definition

Each task identifies:

- The problem or user need.
- The repository and affected files or component.
- One responsible contributor.
- Observable acceptance criteria.
- Dependencies and pending decisions.
- The related user story or business rule, when applicable.
- The pull request and validation evidence when available.

Use the [definition of ready](./definition-of-ready.md) before starting work
and the [definition of done](./definition-of-done.md) before closing it.

## Ownership and collaboration

Each task has one responsible contributor.

Other team members may review, clarify requirements, and help resolve
dependencies. Coordinate changes to shared files before editing them.

Do not duplicate completed work to create additional commits. Check existing
changes and pull requests before starting an assigned task.

## Planning and priorities

Prioritize tasks according to the agreed MVP, dependencies, and delivery dates.

The user-story index records the agreed MVP scope. Priority markers alone do
not confirm that a story belongs to the MVP.

When a scope decision is pending, record it explicitly and continue only with
work that does not depend on that decision.

Changes to the MVP must be reflected in the affected stories, contracts,
data model, and project overview.

## Blocked work

When a task cannot continue:

1. Record the blocker and the decision or input needed.
2. Identify the person or task that can resolve it.
3. Communicate the impact to the affected contributors.
4. Continue independent work when possible.

Do not mark blocked acceptance criteria as completed.

## Review and integration

The contributor opens a focused pull request and describes the change,
validation performed, and unresolved limitations.

Another team member reviews the affected content or behavior.

Follow the repository's Git conventions and branch strategy. Address blocking
review comments and failed checks before integration.

For changes spanning repositories, record the integration order and verify
that the resulting versions work together.

## Progress and evidence

Report completed behavior and remaining work accurately.

Distinguish between:

- Documented or proposed behavior.
- Implemented behavior.
- Verified behavior supported by test or execution evidence.

Record relevant pull requests and validation results with the task.
Do not claim a requirement is verified solely because it appears in a document.

## Improvement

After an integration problem or review, agree on a concrete improvement when
needed and record it as a task or update the relevant working agreement.

---

**Related:** [Definition of ready](./definition-of-ready.md) ·
[Definition of done](./definition-of-done.md) ·
[Git conventions](./git-conventions.md) ·
[User stories](../04-requirements/user-stories.md)
