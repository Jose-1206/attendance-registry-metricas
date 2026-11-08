# att-book-infra

> Attendance Registry - Infrastructure: Compose/IaC, environments and secrets

Part of the **Attendance Registry** full-stack project — team `attendance-registry`, Complementaria III - Desarrollo Fullstack 2026-B.
Governance and documentation live in [`att-book-docs`](https://github.com/code-corhuila/att-book-docs).

## Branching

Three permanent branches. **None of them accepts a direct commit** — you enter through a child
branch and leave through a Pull Request.

```
develop  <--PR--  feat/... fix/... chore/...
qa       <--PR--  develop
main     <--PR--  qa (release)   hotfix/...
```

`develop` is the default branch: start every task from it. `main` requires **1 approval from
`ariel5253`**. On `develop` and `qa` the team sets its own review rule.

Commit and branch naming: `00-governance/git-conventions.md` in `att-book-docs`.
