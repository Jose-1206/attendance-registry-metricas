# Resultados de pruebas e integración — Attendance Registry

**Fecha de evidencias:** 9 de noviembre de 2026
**Institución:** CORHUILA, Métricas y Estándares de Calidad
**Responsable de integración funcional:** José Miguel Aya Peralta
**Consolidación de resultados de backend:** Luis Fernando Perez Chala

## 1. Objetivo y entorno

Verificar las funciones de Attendance Registry y documentar los resultados de pruebas automatizadas y no funcionales empleados en la evaluación de mantenibilidad. El sistema se ejecutó en un entorno local con Docker Compose, PostgreSQL 17, API Spring Boot y portal React/TypeScript servido mediante Nginx.

La validación manual de interfaz y flujos, la suite de frontend, las pruebas de seguridad y el rendimiento fueron documentadas durante la integración del 9 de noviembre. Luis Fernando incorporó posteriormente los resultados completos de pruebas de backend en el PR #8.

## 2. Requisitos funcionales: pruebas manuales

| Código | Escenario | Resultado registrado |
|---|---|---|
| RF-01 | Registro de estudiante | PASS |
| RF-02 | Inicio de sesión y control de roles | PASS |
| RF-03 | Crear curso | PASS |
| RF-04 | Consultar cursos propios | PASS |
| RF-05 | Actualizar curso | PASS |
| RF-06 | Crear sesión académica | PASS |
| RF-07 | Consultar sesiones | PASS |
| RF-08 | Generar QR | PASS |
| RF-09 | Registrar asistencia con QR | PASS |
| RF-10 | Consultar asistencias como docente | PASS |

**Resultado:** 10 de 10 escenarios manuales aprobados, 0 fallos reportados.

## 3. Pruebas automatizadas

| Verificación | Resultado documentado | Referencia |
|---|---|---|
| Backend JUnit/Mockito | **134 pruebas; 134 aprobadas; 0 fallos; 0 errores; 0 omitidas** | `docs/metricas/informe-comparativo-final.md` (PR #8) |
| Arranque Spring Boot (`contextLoads`) | PASS con PostgreSQL y variables locales configuradas | Informe comparativo, apartado 6 |
| Frontend Vitest | **27 de 27 aprobadas** en 4 archivos de pruebas | Registro de integración |
| Frontend Oxlint | 0 errores; 1 advertencia | Registro de integración |
| Compilación frontend | Exitosa | Registro de integración |
| PMD tras refactorización | 0 infracciones con umbral configurado | `docs/metricas/pmd-post-refactor/pmd.xml` |

**Reproducción:** en `sistema/att-book-api`, `./mvnw.cmd -B test` en Windows (con `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET` de prueba configurados). La suite necesita una base PostgreSQL **de pruebas, aislada**, ya que `contextLoads` inicia el contexto de Spring Boot. En `sistema/att-book-portal`, ejecutar `npm test`, `npm run lint` y `npm run build` tras instalar las dependencias.

No se incluyen credenciales ni tokens en este documento.

## 4. Seguridad — RNF-02

**40 de 40** casos documentados aprobaron. Se evaluaron accesos anónimos, credenciales/token inválidos y restricciones por rol mediante comprobaciones de códigos HTTP.

**Alcance:** verifica los escenarios listados; no equivale a un pentest, no incluye inyección, revisión de dependencias ni todas las combinaciones de propiedad de recursos.

Evidencia: [informe RNF-02](seguridad/informe-rnf-02.md) y [CSV de resultados](seguridad/resultados-rnf-02.csv).

## 5. Rendimiento — RNF-03

Con k6 se evaluaron **4 endpoints GET**, **20 usuarios virtuales**, **100 comprobaciones por endpoint** y **400 de 400 comprobaciones aprobadas**, sin errores HTTP. El **P95 HTTP global fue 110,85 ms**, por debajo de 2000 ms.

Los endpoints se solicitaron secuencialmente dentro de cada iteración. No se midieron operaciones de creación, actualización, eliminación o registro de asistencia, ni es una predicción del entorno de producción.

Evidencia: [informe RNF-03](rendimiento/informe-rnf-03.md), [script k6](rendimiento/rnf-03.js) y [resultado JSON](rendimiento/resultado-rnf03.json).

## 6. Infraestructura y advertencias pendientes

El despliegue funcional previo documentó los servicios PostgreSQL, API y portal en ejecución, PostgreSQL saludable y un endpoint `/health` con HTTP 200. La validez del despliegue en una demostración posterior debe comprobarse nuevamente en ese equipo.

Se mantienen dos advertencias del frontend sin bloqueo de compilación: actualización de estado dentro de un efecto de React (`App.tsx`) y un archivo JavaScript generado por Vite superior a 500 kB. Son **oportunidades de mejora** de mantenibilidad y tamaño de carga.

## 7. Conclusión

Las verificaciones registradas respaldan el funcionamiento de los diez requisitos funcionales dentro de los escenarios probados. Las 134 pruebas automatizadas del backend finalizaron correctamente tras configurar el entorno, y la refactorización del PR #6 no dejó infracciones en el reporte PMD posterior. Los resultados de RNF-02 y RNF-03 deben interpretarse dentro de sus límites declarados.
