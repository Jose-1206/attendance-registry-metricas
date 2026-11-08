# 07 — API

## Objective

This section specifies the single public HTTP API exposed by the Attendance
application to its clients.

It defines the conventions used for API design, including resource naming,
HTTP methods, status codes, error responses, authentication requirements, and
OpenAPI contracts.

The API supports the main application capabilities:

- User authentication.
- User information retrieval.
- Course management.
- Class session management.
- QR code generation and validation.
- Attendance registration and history.

---

## Documents

| Document | Purpose | Priority |
|---|---|---|
| [rest-conventions.md](./rest-conventions.md) | Defines REST conventions, resource naming, HTTP methods, status codes, errors, and pagination rules. | ⭐ |
| [contracts/openapi/_template-api.yaml](./contracts/openapi/_template-api.yaml) | Base OpenAPI template used to document API resource contracts. | — |

---

## Out of scope

This section does not define:

- Independent APIs per backend module.
- Microservices communication patterns.
- API gateway or service mesh configuration.
- Distributed transaction patterns such as sagas.

The Attendance backend exposes a single HTTP API, therefore API management
between independent deployable services is not required.

---

## Ready when

- All documents in this section follow the REST conventions defined in
  `rest-conventions.md`.
- API contracts represent the Attendance application resources.
- All endpoints return the shared error response format when failures occur.
- OpenAPI contracts validate according to the OpenAPI specification.
- API documentation remains aligned with the backend implementation.

---

**Related:** [`../06-data/README.md`](../06-data/README.md) · [`../09-modules/README.md`](../09-modules/README.md)
