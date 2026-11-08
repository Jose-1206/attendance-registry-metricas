# 00 — Documentation Rules

> [!NOTE] INSTRUCTIONS
> This document defines when documentation in this repository is considered
> complete. This instructions block is intentionally preserved because this
> document governs the documentation process.

## Document lifecycle

| State | Meaning |
|---|---|
| Empty | The file still contains only generic template content |
| Draft | Attendance-specific content exists but still requires review |
| Done | The document represents the real project and is ready to be integrated |

Adding text to a document does not automatically make it complete.

The content must describe the actual Attendance project and must not contain
invented information added only to fill the template.

## Documentation rules

### 1. One main purpose per document

Each document must focus on the responsibility defined by its repository
section.

### 2. Use Attendance-specific information

Generic examples inherited from the original template must be removed when they
do not represent the Attendance project.

### 3. Do not invent project information

Technologies, requirements, metrics, roles, processes, schedules, and tools
must only be documented when they are actually part of the project or have been
adopted by the team.

### 4. Use consistent terminology

The same project concept must use the same name across related documents.

Core Attendance terminology includes:

- Student
- Teacher
- Course
- Class Session
- QR Code
- Attendance

### 5. Documentation language

Technical project documentation is written in English to maintain consistent
terminology across the repository.

### 6. Relative links

Links between repository documents should use relative paths whenever possible.

### 7. Traceability

When a requirement, user story, rule, or architectural decision already has an
identifier, the same identifier should be used when referencing it from another
document.

### 8. Sensitive information

Documentation must not contain real:

- passwords;
- authentication tokens;
- JWT secrets;
- database credentials;
- private environment values.

### 9. Documentation follows behavior

When a project change modifies documented behavior, the related documentation
must also be reviewed and updated when necessary.

### 10. Diagrams

Mermaid diagrams may be used when flows, relationships, architecture, or other
structures are easier to understand visually.

## Templates

Files whose names begin with `_template-` may intentionally contain
placeholders because they are intended to be copied and completed later.

Completed project documents must not contain unresolved placeholders.

## Review checklist

Before a document is considered Done, verify:

- [ ] The document answers the purpose of its section
- [ ] The content describes Attendance instead of a generic example
- [ ] No project information was invented
- [ ] Terminology is consistent with related documents
- [ ] Relative links work correctly
- [ ] Diagrams render correctly when used
- [ ] No sensitive information is included
- [ ] No unresolved placeholders remain outside template files

---

**Related:** [`definition-of-done.md`](./definition-of-done.md) · [`README.md`](./README.md)
