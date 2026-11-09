# 10 — CI/CD Pipeline

## Document status

This document describes the intended pipeline for Attendance Registry.

Configured jobs, execution results, coverage enforcement, and deployment
automation require verification in the corresponding repositories.

This document does not claim that those checks are implemented or passing.

## Pipeline scope

The backend follows the documented modular monolith architecture.
Its internal modules are built and released together.

The pipeline stages below describe the existing architectural target.
They do not establish that the entire Fullstack system is packaged as
a single container.

## Intended stages

The documented sequence is:

1. Build.
2. Boundary check.
3. Test.
4. Package.
5. Deploy and verify.

| Stage | Purpose | Failure condition |
|---|---|---|
| Build | Compile the application and resolve dependencies. | Compilation fails or a dependency cannot be resolved. |
| Boundary check | Verify the documented internal module boundaries. | A prohibited dependency between modules is detected. |
| Test | Execute the unit and module-integration tests defined in the testing strategy. | A test fails or measured coverage is below NFR-08. |
| Package | Produce a versioned deployable artifact associated with the tested revision. | Packaging fails. |
| Deploy and verify | Deploy the selected artifact and execute the corresponding verification. | Deployment or its verification fails. |

Automated execution of a stage must be supported by its configuration
and execution report.

## Coverage requirement

NFR-08 defines the coverage target:

> At least 80% automated test coverage on changed backend files.

NFR-07 concerns dependency security. It is not the coverage requirement.

The authoritative requirements are documented in
[non-functional requirements](../04-requirements/non-functional.md).

Coverage remains unverified until an applicable report is available.
No additional module-specific percentage is defined here.

## Testing layers

The documented testing layers are:

- `unit`: individual behavior inside a module.
- `module-integration`: behavior through module public interfaces.
- `end-to-end`: behavior of the complete application.

Unit and module-integration tests are intended for the test stage.
End-to-end checks verify the deployed application.

See [testing strategy](../11-quality/testing-strategy.md).

## Execution and promotion

Pull-request checks, deployment triggers, approvals, and artifact promotion
must be compared with the actual repository workflows and governance rules.

A stage described in this document must not be reported as automated
solely because it appears in the intended sequence.

The revision being deployed must be identifiable so that its verification
results can be associated with the correct artifact.

## Verification status

The following items require evidence before the pipeline is considered
operational:

- Build execution.
- Automated module boundary verification.
- Test execution and results.
- Coverage measurement against NFR-08.
- Artifact creation.
- Deployment verification.
- Applicable review and approval controls.

These are verification targets, not a record of completed executions.

## Related documents

- [Boundary enforcement](../05-architecture/boundary-enforcement.md)
- [Testing strategy](../11-quality/testing-strategy.md)
- [Non-functional requirements](../04-requirements/non-functional.md)
- [Environments](./environments.md)
- [Git conventions](../00-governance/git-conventions.md)
