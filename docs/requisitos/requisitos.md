# Requisitos del sistema — Attendance Registry

**Asignatura:** Métricas y Estándares de Calidad — CORHUILA
**Versión:** 2.0, revisión de la implementación integrada (9 de noviembre de 2026)
**Responsable de documentación:** José Miguel Aya Peralta

## 1. Problema, usuarios y alcance

El registro manual de asistencia académica puede producir duplicaciones, errores y dificultades para consultar historiales. Attendance Registry permite a docentes administrar cursos y sesiones, generar códigos QR temporales y revisar asistencias; permite a estudiantes registrarse, autenticarse y registrar asistencia.

El alcance académico comprende **10 requisitos funcionales** y **3 no funcionales**. No incluye notificaciones automáticas, recuperación de contraseñas ni integración con sistemas externos. La implementación funcional se integró en el repositorio académico y fue evaluada mediante pruebas manuales, automatizadas y métricas de calidad.

## 2. Requisitos funcionales y criterios de aceptación

### RF-01. Registro de estudiantes

**Actor:** Estudiante. **Estado:** Implementado; prueba funcional manual PASS.

- Permite registrar un estudiante con datos válidos.
- Rechaza un correo electrónico repetido y los datos obligatorios inválidos.
- No guarda contraseñas en texto plano.
- El estudiante puede autenticarse luego del registro.

### RF-02. Autenticación y control de acceso

**Actores:** Docente y estudiante. **Estado:** Implementado; prueba funcional manual PASS.

- Acepta credenciales correctas y rechaza las incorrectas.
- Requiere autenticación en endpoints protegidos.
- Restringe las operaciones según rol y, cuando corresponde, propiedad del recurso.

### RF-03. Creación de cursos

**Actor:** Docente. **Estado:** Implementado; prueba funcional manual PASS.

- Permite crear cursos con campos válidos y vinculados al docente.
- Rechaza campos obligatorios inválidos.
- Impide que un estudiante cree cursos.
- El curso creado se puede consultar posteriormente.

### RF-04. Consulta de cursos

**Actor:** Docente. **Estado:** Implementado; prueba funcional manual PASS.

- Permite consultar los cursos propios y excluye los ajenos.
- Devuelve una lista vacía válida cuando no existen cursos.

### RF-05. Actualización de cursos

**Actor:** Docente. **Estado:** Implementado; prueba funcional manual PASS.

- Permite modificar datos válidos de un curso propio y conservar los cambios.
- Rechaza modificaciones sobre cursos ajenos y datos inválidos.

### RF-06. Creación de sesiones académicas

**Actor:** Docente. **Estado:** Implementado; prueba funcional manual PASS.

- Permite crear sesiones dentro de un curso propio.
- Valida los datos obligatorios y rechaza cursos ajenos.
- Permite consultar las sesiones creadas.

### RF-07. Consulta de sesiones académicas

**Actores:** Docente autorizado y estudiante inscrito, conforme a los permisos implementados. **Estado:** Implementado; prueba funcional manual PASS.

- Lista las sesiones del curso solicitado sin mezclar cursos.
- Rechaza el acceso de usuarios sin permisos o sin inscripción.

### RF-08. Generación de códigos QR temporales

**Actor:** Docente. **Estado:** Implementado; prueba funcional manual PASS.

- Genera un QR asociado a una sesión propia.
- El código tiene vigencia temporal; un QR vencido no autoriza registrar asistencia.
- Rechaza la generación por usuarios no autorizados.

### RF-09. Registro de asistencia mediante QR

**Actor:** Estudiante. **Estado:** Implementado; prueba funcional manual PASS.

- Permite registrar la asistencia con un QR activo y válido.
- Relaciona estudiante y sesión, y confirma el resultado del intento.
- Rechaza QR inexistentes, inactivos o vencidos.
- Rechaza estudiantes no inscritos y duplicados para una misma sesión.

### RF-10. Consulta de reportes de asistencia

**Actor:** Docente. **Estado:** Implementado; prueba funcional manual PASS.

- Permite consultar asistencias de las sesiones propias.
- Identifica los registros correspondientes a la sesión.
- Rechaza docentes ajenos y devuelve una lista vacía válida sin asistencias.

## 3. Requisitos no funcionales

### RNF-01. Mantenibilidad

- **Criterio de aceptación:** V(G) ≤ 10 en el 100 % de métodos del backend incluidos en el análisis final.
- **Verificación:** PMD 7.17.0, regla `CyclomaticComplexity`, `methodReportLevel=11`.
- **Resultado:** 3 infracciones en la versión funcional anterior a refactorizar y 0 en la posterior. **Cumple según la configuración aplicada.**
- **Evidencia:** [`pmd-final/pmd.xml`](../metricas/pmd-final/pmd.xml), [`pmd-post-refactor/pmd.xml`](../metricas/pmd-post-refactor/pmd.xml) e [informe comparativo](../metricas/informe-comparativo-final.md).

### RNF-02. Seguridad

- **Criterio de aceptación:** los endpoints protegidos deben rechazar solicitudes sin autenticación válida o sin permisos suficientes.
- **Verificación:** prueba documentada sobre escenarios anónimos, token inválido y restricciones de roles.
- **Resultado:** 40 casos ejecutados; 40 aprobados. **Cumple en los escenarios probados**. No se ha acreditado una auditoría completa de todos los riesgos ni de todas las combinaciones de propiedad de recursos.
- **Evidencia:** [informe RNF-02](../pruebas/seguridad/informe-rnf-02.md) y [`resultados-rnf-02.csv`](../pruebas/seguridad/resultados-rnf-02.csv).

### RNF-03. Rendimiento

- **Criterio de aceptación:** P95 ≤ 2000 ms para los endpoints críticos bajo carga documentada.
- **Condiciones:** k6, 20 usuarios virtuales, cuatro endpoints GET, 100 solicitudes/comprobaciones por endpoint (400 en total), entorno Docker local.
- **Resultado:** P95 HTTP global = 110,85 ms, 0 fallos HTTP, 400/400 comprobaciones exitosas. **Cumple en los endpoints y condiciones evaluados**. La prueba no cubre escritura ni garantiza 20 solicitudes simultáneas por endpoint.
- **Evidencia:** [informe RNF-03](../pruebas/rendimiento/informe-rnf-03.md), [`resultado-rnf03.json`](../pruebas/rendimiento/resultado-rnf03.json).

## 4. Matriz de trazabilidad: requisito → componente → evidencia

| Requisito | Componentes implementados | Evidencia | Resultado |
|---|---|---|---|
| RF-01 | `AuthController`, `IdentityService` | Prueba manual RF-01; pruebas de identidad | PASS |
| RF-02 | `AuthController`, `SecurityConfig`, seguridad JWT | Prueba manual RF-02; seguridad RNF-02 | PASS |
| RF-03 | `CourseController`, `CourseService` | Prueba manual RF-03; `CourseServiceTest` | PASS |
| RF-04 | `CourseController`, `CourseService` | Prueba manual RF-04; pruebas de cursos | PASS |
| RF-05 | `CourseController`, `CourseService` | Prueba manual RF-05; pruebas de cursos | PASS |
| RF-06 | `SessionController`, `SessionService` | Prueba manual RF-06; `SessionServiceTest` | PASS |
| RF-07 | `SessionController`, `SessionService` | Prueba manual RF-07; pruebas de sesiones | PASS |
| RF-08 | `QrController`, servicio del módulo `sessions/qr` | Prueba manual RF-08; verificación funcional de QR | PASS |
| RF-09 | `AttendanceController`, `AttendanceService` | Prueba manual RF-09; `AttendanceServiceTest` | PASS |
| RF-10 | `AttendanceController`, `AttendanceService` | Prueba manual RF-10; consulta de asistencias | PASS |
| RNF-01 | Backend Java, servicios y métodos de producción | Informes XML de PMD antes/después | 3 → 0 infracciones |
| RNF-02 | Spring Security, controladores protegidos | 40 casos documentados | 40/40 PASS |
| RNF-03 | API REST, endpoints GET críticos | Script y resultados k6 | P95 global 110,85 ms |

**Fuente de la verificación funcional:** [`../pruebas/integration-tests.md`](../pruebas/integration-tests.md), que registra 10/10 pruebas manuales aprobadas. **Evidencia adicional de backend:** [`../metricas/informe-comparativo-final.md`](../metricas/informe-comparativo-final.md), con 134/134 pruebas automatizadas documentadas después de configurar el entorno de PostgreSQL.

La matriz vincula las pruebas existentes por módulo; no supone que cada criterio de aceptación haya sido objeto de una prueba automatizada individual. La cobertura de casos límite y de seguridad puede ampliarse.

## 5. Estado y limitaciones

Los diez requisitos funcionales del alcance están implementados y cuentan con verificación manual registrada. El criterio RNF-01 se alcanzó en la medición PMD posterior a la refactorización. RNF-02 y RNF-03 se consideran aprobados **solo dentro de sus escenarios documentados**.

La evaluación de mantenibilidad también identificó alertas en WMC, CBO y LCOM* de algunas clases. Son oportunidades de evolución y no se presentan como defectos funcionales confirmados. El informe final académico debe incorporar los resultados reales, riesgos y propuestas priorizadas.
