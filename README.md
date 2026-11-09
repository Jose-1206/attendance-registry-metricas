# Attendance Registry — Métricas y Estándares de Calidad

## 1. Descripción y propósito

**Attendance Registry (Att-book)** es una aplicación web para gestionar cursos, sesiones académicas y asistencia de estudiantes mediante códigos QR. Los docentes administran los cursos y las sesiones, generan códigos QR temporales y consultan los registros; los estudiantes se autentican y registran su asistencia.

Este repositorio corresponde a la evaluación académica de **mantenibilidad y evolución** del sistema para la asignatura **Métricas y Estándares de Calidad**, Corporación Universitaria del Huila (CORHUILA), 2026-B. Se integró una versión del proyecto de Fullstack para medir su calidad interna, realizar una refactorización y comparar resultados.

## 2. Equipo y contribuciones

| Integrante | Usuario de GitHub | Contribución verificable en este repositorio |
|---|---|---|
| José Miguel Aya Peralta | [Jose-1206](https://github.com/Jose-1206) | Requisitos, integración del sistema, validación funcional, seguridad y rendimiento, refactorización RNF-01 y pruebas de asistencia |
| Luis Fernando Perez Chala | [luisfernando-77](https://github.com/luisfernando-77) | Medición y análisis de PMD, CK y LOC, comparaciones, documentación de métricas y consolidación de pruebas del backend |
| Andrés Felipe Calle Gonzalez | AndresCalle09 | Integrante registrado; sin commits propios identificados en el historial de este repositorio a la fecha de esta revisión |

Las contribuciones deben acreditarse mediante commits y Pull Requests; los roles anteriores describen el trabajo verificable en el repositorio académico, no necesariamente la autoría de todos los componentes del proyecto original.

## 3. Requisitos

Se definieron **10 requisitos funcionales** (RF-01 a RF-10) y **3 requisitos no funcionales** (RNF-01 a RNF-03). Los diez RF fueron reportados como aprobados en las pruebas funcionales manuales del sistema integrado.

| Código | Requisito | Resultado documentado |
|---|---|---|
| RF-01 | Registro de estudiantes | PASS |
| RF-02 | Autenticación y control de roles | PASS |
| RF-03 | Creación de cursos | PASS |
| RF-04 | Consulta de cursos del docente | PASS |
| RF-05 | Actualización de cursos | PASS |
| RF-06 | Creación de sesiones académicas | PASS |
| RF-07 | Consulta de sesiones | PASS |
| RF-08 | Generación de códigos QR temporales | PASS |
| RF-09 | Registro de asistencia con QR | PASS |
| RF-10 | Consulta de asistencias por sesión | PASS |

| Código | Criterio | Evidencia y alcance |
|---|---|---|
| RNF-01 | V(G) ≤ 10 para los métodos del backend incluidos en el análisis | PMD 7.17.0: **3 infracciones antes, 0 después** de la refactorización; cumple según el ruleset utilizado |
| RNF-02 | Rechazo de accesos no autorizados | **40/40 escenarios definidos** aprobados; las pruebas no constituyen una auditoría integral de seguridad |
| RNF-03 | Percentil 95 (P95) ≤ 2000 ms | Prueba k6 con 20 usuarios virtuales, cuatro endpoints GET y 400 comprobaciones exitosas; P95 HTTP global de **110,85 ms**, válido para ese entorno de prueba |

Ver [requisitos, criterios de aceptación y matriz de trazabilidad](docs/requisitos/requisitos.md).

## 4. Arquitectura y tecnologías

Arquitectura **cliente-servidor con backend monolítico modular organizado en capas**:

- **Frontend:** React y TypeScript, interfaz de docentes y estudiantes.
- **API REST:** Java 17 y Spring Boot, controladores y reglas de negocio.
- **Seguridad:** Spring Security y JWT, autenticación y autorización.
- **Persistencia:** Spring Data JPA y PostgreSQL 17.
- **Infraestructura:** Docker Compose; portal servido con Nginx.
- **Pruebas:** JUnit y Mockito en backend, Vitest en frontend; k6 para rendimiento.
- **Métricas:** PMD 7.17.0, CK de Mauricio Aniche y conteo de LOC con PowerShell.

Se utiliza Java en el backend por su orientación a objetos y por la posibilidad de aplicar CK y PMD; TypeScript con React permite desarrollar y verificar la interfaz. PostgreSQL permite persistencia relacional y restricciones de integridad.

El diagrama, módulos, decisiones y compromisos están en [docs/arquitectura/arquitectura.md](docs/arquitectura/arquitectura.md).

## 5. Organización del repositorio

```text
attendance-registry-metricas/
├── README.md
├── docs/
│   ├── arquitectura/arquitectura.md
│   ├── requisitos/requisitos.md
│   ├── metricas/
│   │   ├── definicion-metricas.md
│   │   ├── informe-comparativo-final.md
│   │   ├── ck-inicial/           # Medición de la base inicial
│   │   ├── ck-final/             # Estado integrado antes de refactorizar
│   │   ├── ck-post-refactor/     # Estado posterior a refactorizar
│   │   ├── pmd-final/
│   │   └── pmd-post-refactor/
│   ├── pruebas/
│   │   ├── integration-tests.md
│   │   ├── seguridad/
│   │   └── rendimiento/
│   └── integracion/versiones-origen.md
└── sistema/
    ├── att-book-api/
    ├── att-book-db/
    ├── att-book-infra/
    ├── att-book-portal/
    └── att-book-docs/
```

## 6. Instalación y ejecución local

**Requisitos:** Git, Docker Desktop con Docker Compose y puertos locales disponibles. Para ejecutar las herramientas fuera de Docker se requieren Java 17 y Node.js/npm.

1. Clonar este repositorio y entrar al directorio de infraestructura:

   ```powershell
   git clone https://github.com/Jose-1206/attendance-registry-metricas.git
   cd attendance-registry-metricas/sistema/att-book-infra
   ```

2. Crear un archivo `.env` **local, no versionado**, dentro de `att-book-infra`, con dos variables:

   ```dotenv
   DB_PASSWORD=CAMBIAR_POR_CONTRASENA_LOCAL_SEGURA
   JWT_SECRET=CAMBIAR_POR_SECRETO_VALIDO_LOCAL
   ```

   Usar valores personales para pruebas. La clave JWT debe respetar el formato y longitud aceptados por la API. No publicar credenciales o tokens.

3. Levantar los tres servicios:

   ```powershell
   docker compose -f compose.fullstack.yaml up -d --build
   docker compose -f compose.fullstack.yaml ps
   ```

4. Abrir el portal en **http://localhost:8086**. Para preparar datos de demostración, seguir el orden de scripts de semillas documentado en [`sistema/att-book-infra/README.md`](sistema/att-book-infra/README.md).

5. Para consultar errores de la API:

   ```powershell
   docker compose -f compose.fullstack.yaml logs --tail=60 api
   ```

6. Para detener sin borrar la base de datos:

   ```powershell
   docker compose -f compose.fullstack.yaml down
   ```

   **No usar `down -v`** si se deben conservar los datos.

## 7. Pruebas y reproducibilidad

Desde `sistema/att-book-portal`:

```powershell
npm ci
npm test
npm run lint
npm run build
```

Desde `sistema/att-book-api`:

```powershell
.\mvnw.cmd -B test
.\mvnw.cmd -B pmd:pmd
.\mvnw.cmd -B pmd:check
```

La prueba de arranque `AttBookApiApplicationTests.contextLoads` requiere una **base de datos de pruebas PostgreSQL configurada**, con las variables de entorno `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET`. No se debe ejecutar contra una base con datos que se quieran conservar. El comando Maven anterior funcionará cuando estas dependencias estén configuradas; consultar [informe de métricas](docs/metricas/informe-comparativo-final.md) para el resultado documentado.

**Resultados documentados:** 134/134 pruebas de backend exitosas con entorno configurado, 27/27 pruebas de frontend exitosas, 10/10 pruebas funcionales manuales exitosas. Oxlint informó 0 errores y 1 advertencia; la compilación del frontend fue exitosa.

Las evidencias de las pruebas están en [docs/pruebas/integration-tests.md](docs/pruebas/integration-tests.md), [RNF-02](docs/pruebas/seguridad/informe-rnf-02.md) y [RNF-03](docs/pruebas/rendimiento/informe-rnf-03.md).

## 8. Métricas y resultados

El alcance principal de medición es el código Java de producción en `sistema/att-book-api/src/main/java`. El portal web se evaluó adicionalmente con pruebas y lint, pero no se incluyó en la suite CK.

| Métrica | Resultado registrado |
|---|---|
| Complejidad ciclomática PMD | 3 infracciones antes y 0 después de la refactorización |
| LOC física | 1028 líneas en la base inicial y 2473 en la versión funcional ampliada; son **versiones de distinto alcance** |
| CK: WMC medio | 4,79 antes; 5,04 después de la refactorización |
| CK: CBO medio | 5,60 antes; 5,60 después |
| CK: RFC medio | 5,62 antes; 5,88 después |
| CK: LCOM no normalizado medio | 2,56 antes; 4,94 después |

**Nota:** la disminución de V(G) por método no implica automáticamente mejorar el diseño completo. Según `class.csv`, `SessionService` y `AttendanceService` superan el umbral de WMC tras la refactorización, y varias clases presentan acoplamiento CBO elevado. Para comparar con el umbral 0,7 debe utilizarse **LCOM\*** normalizado, no el LCOM bruto.

La descripción de métricas y los datos fuente se encuentran en [docs/metricas/](docs/metricas/) y el [informe comparativo](docs/metricas/informe-comparativo-final.md).

## 9. Refactorización y mejoras propuestas

En el PR [#6](https://github.com/Jose-1206/attendance-registry-metricas/pull/6) se aplicó **Extract Method** en:

- `SessionService.validateRequest` (PMD inicial: V(G)=17).
- `AttendanceService.registerAttendance` (PMD inicial: V(G)=15).
- `SessionService.requireCourseAccess` (PMD inicial: V(G)=11).

Se agregaron pruebas automatizadas de asistencia, conservando los contratos y las validaciones. El reporte PMD posterior registró cero infracciones con el umbral configurado.

**Próximas mejoras, no implementadas en esta entrega:** evaluar extracción de clases/responsabilidades en `SessionService` y `AttendanceService`, disminuir dependencias de controladores con CBO elevado, ampliar las pruebas de autorización por recurso y rendimiento de operaciones de escritura, y revisar la advertencia de React y el tamaño de bundle del portal.

## 10. Resultados, informe y estado

- [Informe comparativo de métricas](docs/metricas/informe-comparativo-final.md).
- [Requisitos y trazabilidad](docs/requisitos/requisitos.md).
- [Arquitectura](docs/arquitectura/arquitectura.md).
- [Registro de versiones integradas](docs/integracion/versiones-origen.md).
- [Pruebas de integración](docs/pruebas/integration-tests.md).

**Estado de la aplicación:** funcionalidades del alcance RF-01 a RF-10 implementadas y verificadas mediante pruebas manuales. **Estado de la entrega académica:** pendiente de incorporar el informe PDF definitivo y publicar una etiqueta/Release final.

El PDF se incorporará en `docs/` y enlazará desde esta sección cuando Luis Fernando lo termine; no se presenta un enlace inexistente como evidencia.

## 11. Origen, autoría y licencia

Esta adaptación reutiliza componentes de **Attendance Registry** desarrollados previamente en el curso de Fullstack. Los commits de referencia de cada módulo se identifican en [docs/integracion/versiones-origen.md](docs/integracion/versiones-origen.md).

Los repositorios de origen se identifican como privados. **La presencia del código aquí no prueba por sí sola que su publicación pública esté autorizada.** Se debe verificar la autorización de los titulares y acordar la licencia adecuada antes de declarar libertad de redistribución. No se declara una licencia que no exista.

**Asignatura:** Métricas y Estándares de Calidad. **Docente:** José Miguel Llanos Mosquera. **Institución:** CORHUILA.
