# Attendance Registry-att-book

## MVP overview

Attendance Registry is a web application for recording class attendance with
temporary QR codes. It addresses the difficulty of keeping attendance records
organized by class session and making them available to students and teachers.

## Project team

**GitHub organization:** `code-corhuila`  
**GitHub team:** `attendance-registry`

| Student | GitHub username |
| --- | --- |
| Juan Jose Guzman Bohorquez | `juanjoGuzmanBohorquez` |
| Jose Miguel Aya Peralta | `Jose-1206` |
| Luis Fernando Perez Chala | `luisfernando-77` |
| Carlos Andrés Zuluaga Acuña | carloszuluaga-20 |
| Rosfeller David Castillo Garzón | feller2006 |

## MVP scope

**Proposed MVP stories:** `HU-01`, `HU-02`, and `HU-04` through `HU-11`.  
**Proposed for a later release:** `HU-03`, viewing the authenticated profile.

The proposed scope includes account registration, sign-in, course and session
management, temporary QR generation, attendance registration, personal attendance
history, and teacher review of attendance by session (`HU-11`).

| Role | Proposed permissions |
| --- | --- |
| Student | Record attendance using a valid session QR code and view their own attendance history. |
| Teacher | Create and view their own courses and class sessions, generate temporary QR codes for those sessions, and review attendance for those sessions. |

**Business rule:** The system rejects an attendance submission if the QR code
has expired or if the student has already recorded attendance for the same
class session.

See the [user stories](./04-requirements/user-stories.md) and
[domain rules](./02-domain/entities-and-rules.md).

## Entities and relationships

| Entity | Relationship | Documented operations and open decisions |
| --- | --- | --- |
| User | A student can have attendance records for multiple sessions. | Account registration and authenticated profile lookup are in the backlog. Confirm update and delete operations and their permissions. |
| Course | A course can have multiple class sessions. | Create and read are documented. Confirm update and delete, including what happens when a course already has sessions. |
| Class Session | A class session belongs to a course and can have attendance records. | Create and read are documented. Confirm update and delete and how they affect existing attendance records. |
| QR Code | A temporary QR code is issued for a class session. | QR generation is documented. Confirm which operations will count toward full CRUD. |
| Attendance | An attendance record links a student to a class session. | Registration and personal history are documented. Teacher review by session is in the proposed MVP. Confirm correction and deletion and their permissions. |

The course guide requires full CRUD for five entities for a five-student team,
with at least two related entities. Full CRUD means create, read, update, and
delete. The final list of entities and permissions is pending team confirmation.

See the [data model](./06-data/models.md) for the mapping between entities,
database tables, and modules.

## Main API operations

The target base path for the versioned API contract is `/api/v1`. The API owner
will confirm the final routes and which operations are available in Spring Boot.

| Operation | Proposed access | Contract status |
| --- | --- | --- |
| Register an account (`HU-01`) | Public | Final method and path pending confirmation |
| Sign in (`HU-02`) | Public | Target route `POST /api/v1/auth/login`, pending backend verification |
| Create and list courses (`HU-04`, `HU-05`) | Teacher, own courses | Create and read documented |
| Create and list class sessions (`HU-06`, `HU-07`) | Teacher, own courses | Create and read documented |
| Generate a temporary QR code (`HU-08`) | Teacher, own sessions | Documented |
| Record attendance (`HU-09`) | Student | Documented |
| View personal attendance history (`HU-10`) | Student, own records | Documented |
| Review attendance by session (`HU-11`) | Teacher, own sessions | Proposed for the MVP, contract operation pending |
| Complete CRUD for the selected entities | Pending confirmation | Pending scope and contract confirmation |

See the [REST conventions](./07-api/rest-conventions.md) and
[OpenAPI contracts](./07-api/contracts/openapi/).

## Project repositories

| Repository | Purpose and technology | Responsible person |
| --- | --- | --- |
| [att-book-docs](https://github.com/code-corhuila/att-book-docs) | Markdown documentation and OpenAPI contracts | Jose Miguel Aya Peralta |
| [att-book-api](https://github.com/code-corhuila/att-book-api) | Spring Boot REST API | Jose Miguel Aya Peralta |
| [att-book-portal](https://github.com/code-corhuila/att-book-portal) | Web portal. SPA framework pending confirmation. | Jose Miguel Aya Peralta |
| [att-book-db](https://github.com/code-corhuila/att-book-db) | Database schema and migrations. Database engine pending confirmation. | Jose Miguel Aya Peralta |
| [att-book-infra](https://github.com/code-corhuila/att-book-infra) | Deployment configuration. Deployment tools pending confirmation. | Jose Miguel Aya Peralta |

---

# Monolith Governance Framework

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![version 2.0.0](https://img.shields.io/badge/version-2.0.0-blue.svg)](CHANGELOG.md)
[![jesusarielgb-works](https://img.shields.io/badge/org-jesusarielgb--works-lightgrey.svg)](https://github.com/jesusarielgb-works)

> A Software Design Documentation framework for teams building **one deployable
> application**: fifteen sections saying what to write down, in what order,
> and when each is finished.

**Author:** [Jesus Ariel Gonzalez Bonilla](https://github.com/jesusarielgb-works)

## Scope

- One application, one build, one deployment — organised internally as modules
- Module boundaries drawn in the business language and enforced by the build
- One relational database, with exactly one owning module per table
- One public API surface, one pipeline, one process to observe and operate
- Stack guides for Java/Spring, Node/TypeScript, Python/Django and PHP/Laravel

The growth path this framework supports is **toward the modular monolith**: firmer
boundaries inside the same deployable, not more deployables.

## Out of scope

- Service decomposition and inter-service communication
- Distributed transactions, service mesh, api gateway, service discovery
- Per-service deployment, versioning and release trains
- Resilience patterns for remote calls, such as the circuit breaker or the saga

Distributed-system concerns belong to the
[microservices governance framework](https://github.com/jesusarielgb-works/microservices-governance-framework).

## How to use this framework

1. Read [`00-sdd-guide.md`](./00-sdd-guide.md) — the four phases, their gates, and the
   week-by-week fill-in order.
2. Copy this repository into your project as its documentation root, or fork it.
3. Choose your stack guide in [`_stacks/`](./_stacks/README.md) and adopt its folder
   tree and commands before Week 1.
4. Agree [`00-governance/`](./00-governance/README.md) first — it is what every later
   section is measured against.
5. Fill the remaining sections in the guide's order, one phase at a time.
6. Delete a document's INSTRUCTIONS block only once that document is complete and a
   second person has reviewed it.
7. Update the affected document in the same pull request as the code it describes.

## How the sections depend on each other

```mermaid
flowchart TD
    GV["00-governance"] -.-> CX["01-context"]
    CX --> DM["02-domain"] --> PR["03-product"] --> RQ["04-requirements"] --> AR["05-architecture"]
    ST["_stacks"] -.-> AR
    AR --> DA["06-data"] --> MD["09-modules"]
    AR --> AP["07-api"] --> MD
    AR --> UM["08-uml"] --> MD
    MD --> UX["12-ux-ui"]
    RQ -.-> UX
    MD --> DO["10-devops"]
    MD --> QA["11-quality"]
    MD --> OP["13-operations"]
```

A solid arrow means the target section cannot be answered honestly until the source
one is — the order `00-sdd-guide.md` fills them in. A dotted arrow is the weaker
relationship, and covers two cases: a cross-cutting section, read once and applied
throughout (`00-governance`, `_stacks`), or an input that informs a section without
gating its order (`04-requirements` feeding `12-ux-ui`).

## The fifteen sections

| Section | The question it answers |
|---|---|
| [`00-governance/`](./00-governance/README.md) | How does this team plan, branch, review, close and secure its work? |
| [`01-context/`](./01-context/README.md) | Why does this system exist, for whom, and where are its edges? |
| [`02-domain/`](./02-domain/README.md) | What is the business, and where do the module boundaries fall? |
| [`03-product/`](./03-product/README.md) | Where is the product going, and which problem justifies building it? |
| [`04-requirements/`](./04-requirements/README.md) | What must the system do, measurably — and how well? |
| [`05-architecture/`](./05-architecture/README.md) | What internal shape does the single deployable take, and who enforces it? |
| [`06-data/`](./06-data/README.md) | Which module owns which table, and how does the schema change? |
| [`07-api/`](./07-api/README.md) | What does the one public API look like to the clients that call it? |
| [`08-uml/`](./08-uml/README.md) | What do the critical structures and flows look like as diagrams? |
| [`09-modules/`](./09-modules/README.md) | What does each module own and expose, what has it decided, and how is it run? |
| [`10-devops/`](./10-devops/README.md) | How does one build artifact reach each environment, and on what conditions? |
| [`11-quality/`](./11-quality/README.md) | What is tested, at which of the three layers, and written when? |
| [`12-ux-ui/`](./12-ux-ui/README.md) | What keeps the screens consistent with each other? *(skip if there are none)* |
| [`13-operations/`](./13-operations/README.md) | How does the running application show its state, and what happens when it fails? |
| [`_stacks/`](./_stacks/README.md) | What does all of the above look like on disk, in my language? |

## How to cite

> Gonzalez Bonilla, J. A. (2026). *Monolith Governance Framework* (v2.0.0).
> jesusarielgb-works. https://github.com/jesusarielgb-works/monolith-governance-framework

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

MIT — Jesus Ariel Gonzalez Bonilla. See [LICENSE](LICENSE).
