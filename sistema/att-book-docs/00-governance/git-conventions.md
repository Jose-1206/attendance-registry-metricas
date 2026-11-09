# 00 — Git Conventions

## Purpose

These conventions keep Attendance Registry documentation changes organized,
traceable, and reviewable.

## Branch strategy

The `main` branch contains the integrated documentation of `att-book-docs`.

Create a short-lived branch for each documentation task and submit a pull
request targeting `main`.

Changes in other Attendance Registry repositories follow their own documented
branch strategy. This document does not define their release workflow.

## Branch naming

Use lowercase words separated by hyphens:

```text
docs/<short-description-in-kebab-case>
```

Examples:

```text
docs/fix-hu-01-location
docs/complete-git-conventions
docs/update-story-traceability
```

Keep each branch focused on one clear task.

## Commits

Write commit messages in English using this format:

```text
docs: <short description>
```

Examples:

```text
docs: fix HU-01 location and restore story links
docs: complete documentation Git conventions
```

Each commit must contain a meaningful change related to its stated purpose.
Avoid unrelated files, duplicate content, and empty commits made only to
increase contribution counts.

Before committing:

1. Review the changed files with `git diff`.
2. Stage only the files belonging to the task.
3. Review the staged changes with `git diff --cached`.
4. Run `git diff --cached --check`.
5. Verify links and Markdown rendering affected by the change.

## Authorship

Each contributor uses their own Git identity and GitHub account.

Before committing, verify:

```bash
git config user.name
git config user.email
```

The configured identity must belong to the person making the contribution.
Do not change another contributor's authorship.

Each student manually commits and pushes their own work and opens their own
pull request. Review contribution evidence through the commits and pull
requests associated with the change.

## Pull requests

Open pull requests from the task branch into `main`.

Use an English title that describes the resulting change. Include:

- The problem addressed.
- The files or behavior changed.
- The validation performed.
- Any pending decision or dependency.

Request review from another team member before merging.

The reviewer checks the accuracy of the content, relative links, consistency
with related documents, and any available automated checks.

Do not describe proposed functionality as implemented without supporting
evidence.

## Conflicts and integration

Coordinate changes when more than one task affects the same file.

If a pull request has conflicts:

1. Update the task branch with the latest `main`.
2. Review both versions of each conflicting section.
3. Resolve the content with the affected contributor when needed.
4. Remove all conflict markers.
5. Repeat the validation before pushing the resolution.

Never discard another contributor's changes without reviewing their purpose.

Merge after review and resolution of blocking issues. After integration,
update the local `main` branch. Delete the task branch when it is no longer
needed.

## Completion checklist

- [ ] The change has a clear purpose.
- [ ] Only related files are included.
- [ ] Commit messages and the pull request title are in English.
- [ ] Authorship identifies the actual contributor.
- [ ] Relative links and Markdown formatting have been checked.
- [ ] Staged changes pass `git diff --cached --check`.
- [ ] Another team member has reviewed the pull request.
- [ ] Pending decisions are stated explicitly.

---

**Related:** [Documentation rules](./documentation-rules.md) ·
[Definition of ready](./definition-of-ready.md) ·
[Definition of done](./definition-of-done.md)
