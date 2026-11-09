# Registro de versiones de origen — Attendance Registry

## 1. Información general

**Proyecto:** Attendance Registry
**Asignatura:** Métricas y Estándares de Calidad
**Fecha de integración:** 9 de noviembre de 2026
**Rama de integración:** chore/sync-attendance-registry

## 2. Objetivo

Registrar las versiones de los componentes originales
utilizados para actualizar Attendance Registry dentro
del repositorio académico.

Esta información permite identificar el código base
utilizado en las pruebas, mediciones y refactorizaciones
posteriores.

## 3. Versiones de origen

| Componente | Repositorio de origen | Commit |
|---|---|---|
| API | code-corhuila/att-book-api | b4c5f23c0bde5190d450e869bd7ee3bbd688f782 |
| Portal | code-corhuila/att-book-portal | 9cfe0b7c8d54a9a586c89f0b1338046a3715bc65 |
| Base de datos | code-corhuila/att-book-db | 5f6fc268e132f4940c75005872b6248ce5e792bf |
| Infraestructura | code-corhuila/att-book-infra | 7c5dae09195969340841467e89ea6bb1b21791a9 |
| Documentación | code-corhuila/att-book-docs | 428e823372f714b42507fe6824de78c8d18f2b58 |

## 4. Estructura integrada

Los componentes se encuentran organizados dentro
del directorio sistema/ del repositorio académico.

- sistema/att-book-api
- sistema/att-book-portal
- sistema/att-book-db
- sistema/att-book-infra
- sistema/att-book-docs

## 5. Ajustes realizados

Durante el proceso de integración se incorporaron
los componentes actualizados y se corrigieron las
pruebas automatizadas del módulo de inscripciones.

Los ajustes de pruebas permitieron actualizar las
expectativas de la interfaz en español y aislar
las dependencias del historial de asistencias.

Por lo tanto, los commits de origen identifican
las versiones utilizadas como referencia, pero
no implican que todos los archivos integrados
sean copias idénticas.

## 6. Validaciones realizadas

- Pruebas automatizadas del portal: 27/27 aprobadas.
- Pruebas funcionales manuales: 10/10 aprobadas.
- Compilación del frontend: exitosa.
- Análisis estático del portal: 0 errores y 1 advertencia.
- Infraestructura Docker: tres servicios en ejecución.
- Base de datos PostgreSQL: estado saludable.
- Endpoint de salud del portal: HTTP 200 OK.

## 7. Validaciones pendientes

- Ejecución completa de las pruebas automatizadas
  del backend.
- Mediciones de complejidad ciclomática.
- Medición de líneas de código.
- Aplicación de métricas CK.
- Pruebas de rendimiento y seguridad.
- Documentación de refactorizaciones y comparación
  de resultados.

## 8. Consideraciones de publicación

Los repositorios de origen son privados.

La integración académica debe respetar las
autorizaciones de acceso, reutilización y publicación
del código y la documentación originales.

El registro de los commits de origen no constituye
por sí mismo autorización para redistribuir
estos componentes públicamente.
