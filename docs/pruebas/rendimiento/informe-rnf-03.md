# Informe de rendimiento RNF-03 — Attendance Registry

## 1. Información general

- Proyecto: Attendance Registry.
- Asignatura: Métricas y Estándares de Calidad.
- Fecha: 9 de noviembre de 2026.
- Herramienta: Grafana k6.
- Entorno: Docker Compose local.
- Versión base: commit 3233866.
- Responsable: Integración y validación de rendimiento.

## 2. Objetivo

Evaluar los tiempos de respuesta de cuatro endpoints
de consulta de Attendance Registry mediante una prueba
de carga con 20 usuarios virtuales.

## 3. Criterios de aceptación

- Usuarios virtuales: 20.
- Solicitudes mínimas por endpoint: 100.
- Percentil 95 de respuesta: menor de 2000 ms.
- Porcentaje de errores HTTP: 0 %.
- Respuestas esperadas: HTTP 200.

## 4. Configuración de la prueba

Se utilizó el ejecutor per-vu-iterations de k6.

- Usuarios virtuales: 20.
- Iteraciones por usuario: 5.
- Iteraciones completadas: 100.
- Endpoints evaluados: 4.
- Pausa por iteración: 0,5 segundos.
- Autenticación previa: docente y estudiante.

Los endpoints se consultaron secuencialmente dentro
de cada iteración de cada usuario virtual.

## 5. Resultados obtenidos

| Endpoint | Solicitudes | P95 | Errores | Resultado |
|---|---:|---:|---:|---|
| Cursos del docente | 100 | 106,83 ms | 0 % | PASS |
| Historial de asistencias | 100 | 57,83 ms | 0 % | PASS |
| Inscripciones del estudiante | 100 | 83,30 ms | 0 % | PASS |
| Perfil del estudiante | 100 | 116,39 ms | 0 % | PASS |

## 6. Resultados generales

- Solicitudes de consulta: 400.
- Solicitudes HTTP totales: 402.
- Comprobaciones realizadas: 400.
- Comprobaciones aprobadas: 400.
- Comprobaciones fallidas: 0.
- Usuarios virtuales: 20.
- Iteraciones completadas: 100.
- Iteraciones interrumpidas: 0.
- Tiempo promedio HTTP global: 31,79 ms.
- Percentil 95 HTTP global: 110,85 ms.
- Tiempo máximo HTTP global: 608,42 ms.
- Solicitudes HTTP fallidas: 0.
- Umbrales configurados: todos aprobados.

## 7. Análisis

Los cuatro endpoints evaluados registraron tiempos
P95 inferiores al límite de 2000 ms.

El mayor P95 por endpoint correspondió a la consulta
del perfil del estudiante, con 116,39 ms.

La prueba finalizó sin errores HTTP y todas las
comprobaciones de respuesta obtuvieron HTTP 200.

Estos resultados muestran un comportamiento satisfactorio
de las operaciones de consulta bajo la carga configurada.

## 8. Limitaciones

La prueba se realizó en un entorno local con Docker.

Los resultados no deben interpretarse como mediciones
de un entorno de producción.

Aunque participaron 20 usuarios virtuales, las cuatro
solicitudes de cada iteración se ejecutaron de forma
secuencial. Por lo tanto, no se garantiza que cada
endpoint haya recibido 20 solicitudes simultáneas
en un mismo instante.

No se evaluaron operaciones de creación, actualización,
eliminación ni registro de asistencia.

Las comprobaciones verificaron el estado HTTP 200,
pero no validaron exhaustivamente el contenido JSON.

## 9. Conclusión

Attendance Registry cumple el criterio de rendimiento
RNF-03 para los cuatro endpoints de consulta evaluados
bajo las condiciones de esta prueba.

Se recomienda ampliar posteriormente la cobertura
a operaciones de escritura y repetir las mediciones
para evaluar la estabilidad de los resultados.

## 10. Evidencias

- Script de carga: rnf-03.js.
- Resultados de k6: resultado-rnf03.json.
- Evidencia adicional: salida de consola de la ejecución.
