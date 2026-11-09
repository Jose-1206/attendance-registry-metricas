# Registro de versiones de origen — Attendance Registry

**Fecha de integración:** 9 de noviembre de 2026  
**Rama de integración original:** `chore/sync-attendance-registry`  
**Repositorio académico:** `Jose-1206/attendance-registry-metricas`

## 1. Objetivo

Identificar los commits originales desde los que se integró el sistema de Fullstack y diferenciar esa integración de las modificaciones académicas posteriores. Los repositorios de origen se identifican como **privados**.

## 2. Versiones originales registradas

| Componente | Repositorio de origen | Commit |
|---|---|---|
| API | `code-corhuila/att-book-api` | `b4c5f23c0bde5190d450e869bd7ee3bbd688f782` |
| Portal | `code-corhuila/att-book-portal` | `9cfe0b7c8d54a9a586c89f0b1338046a3715bc65` |
| Base de datos | `code-corhuila/att-book-db` | `5f6fc268e132f4940c75005872b6248ce5e792bf` |
| Infraestructura | `code-corhuila/att-book-infra` | `7c5dae09195969340841467e89ea6bb1b21791a9` |
| Documentación | `code-corhuila/att-book-docs` | `428e823372f714b42507fe6824de78c8d18f2b58` |

## 3. Estructura integrada

- `sistema/att-book-api`
- `sistema/att-book-portal`
- `sistema/att-book-db`
- `sistema/att-book-infra`
- `sistema/att-book-docs`

Los cambios incorporados durante la integración y las pruebas del portal hacen que algunos archivos no sean copias idénticas a los originales.

## 4. Cambios y verificaciones posteriores

| Hito | Referencia | Resultado documentado |
|---|---|---|
| Integración actualizada | PR #5 | Sistema full-stack incorporado a la rama principal |
| Refactorización y pruebas de asistencia | PR #6 | PMD: de 3 a 0 infracciones |
| Métricas finales y comparación | PR #7 | Informes CK, PMD y LOC registrados |
| Validación final de pruebas de backend | PR #8 | 134 de 134 pruebas aprobadas en entorno configurado |

También se registraron 10/10 pruebas funcionales manuales, 27/27 pruebas de frontend, 40/40 escenarios de seguridad y la carga RNF-03 descrita en los documentos de prueba.

## 5. Consideraciones de autoría y publicación

Este registro sirve como trazabilidad técnica y **no constituye autorización de redistribución pública** de código privado. Deben verificarse los permisos de reutilización y publicación, reconocer a los titulares correspondientes y definir correctamente los términos de licencia. El repositorio académico es público; este punto necesita revisión por los responsables antes de dar por cerrado el apartado de licencia.

## 6. Referencias

- [Pruebas e integración](../pruebas/integration-tests.md).
- [Métricas y refactorización](../metricas/informe-comparativo-final.md).
- [Requisitos](../requisitos/requisitos.md).
