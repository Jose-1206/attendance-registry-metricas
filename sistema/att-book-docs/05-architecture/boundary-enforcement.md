# 05 — Boundary Enforcement

## What is being enforced

Attendance follows one architectural rule:

**A module may depend on another module only through its public API and must never access another module's internal implementation directly.**

The module boundaries are defined in:

- [`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md)
- [`./module-structure.md`](./module-structure.md)

The architectural decision that defines these module boundaries is documented in:

- [`./decisions/records/ADR-002-attendance-module-boundaries.md`](./decisions/records/ADR-002-attendance-module-boundaries.md)

The Attendance application contains the following internal modules:

- `identity`
- `academic`
- `class-sessions`
- `attendance`

These modules belong to the same modular monolith application. They are not independent services. Each module owns its own data and exposes only the functionality required by other modules through a public API.

For example, the `attendance` module can use public functionality from:

- `identity` to identify students and user information.
- `academic` to validate course enrollment.
- `class-sessions` to validate class sessions and QR codes.

However, the `attendance` module must not access internal implementation details from these modules.

---

## Tool used by Attendance

Attendance uses **Java with Spring Boot**.

The decision to enforce module boundaries with ArchUnit is supported by:

[`./decisions/records/ADR-002-attendance-module-boundaries.md`](./decisions/records/ADR-002-attendance-module-boundaries.md)

| Stack | Tool | Rule |
|---|---|---|
| Java / Spring Boot | ArchUnit | Classes outside a module must not directly depend on another module's internal packages |

ArchUnit is responsible for verifying that dependencies between modules respect the defined architecture rules.

The general stack information is documented in:

[`../_stacks/README.md`](../_stacks/README.md)

---

## Boundary rules

The following rules define the dependency restrictions between Attendance modules.

Example: the `attendance` module cannot access internal classes from the `identity` module.

```java
@ArchTest
static final ArchRule attendance_must_not_access_identity_internal =
    noClasses()
        .that().resideInAPackage("..attendance..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..identity.internal..");

```

The example above illustrates an architectural restriction using ArchUnit.
It prevents classes in the `attendance` module from directly depending on
internal classes belonging to the `identity` module.

## Continuous integration verification

The architecture requires module boundary checks to run automatically
on every pull request.

The implementation of these checks in the backend CI pipeline must be
verified before this requirement can be considered complete.
