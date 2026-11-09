# Resultados de las pruebas de integración — Attendance Registry

## 1. Información general

**Proyecto:** Attendance Registry — Métricas y Estándares de Calidad

**Institución:** Corporación Universitaria del Huila (CORHUILA)

**Asignatura:** Métricas y Estándares de Calidad

**Fecha de ejecución:** 9 de noviembre de 2026

**Responsable:** José Miguel Aya Peralta

**Tipo de pruebas:** Pruebas funcionales manuales de integración

## 2. Entorno de pruebas

El sistema fue ejecutado localmente mediante Docker Compose.

Tecnologías utilizadas:

- Frontend: React y TypeScript.
- Backend: Java y Spring Boot.
- Base de datos: PostgreSQL 17.
- Infraestructura: Docker y Docker Compose.
- Pruebas automatizadas del frontend: Vitest.

Se verificó que los contenedores correspondientes a la base de datos, la API y el portal web estuvieran en ejecución.

Adicionalmente, el endpoint de salud del portal respondió con el código HTTP 200 OK.

## 3. Objetivo de las pruebas

Comprobar que los principales módulos de Attendance Registry funcionan correctamente después de integrar la versión actualizada del sistema al repositorio de Métricas y Estándares de Calidad.

Las pruebas permiten verificar los requisitos funcionales establecidos y confirmar la comunicación entre la interfaz web, la API y la base de datos.

## 4. Resultados de los requisitos funcionales

| Código | Prueba realizada | Resultado |
|---|---|---|
| RF-01 | Registro de un estudiante nuevo | PASS |
| RF-02 | Inicio de sesión y separación de roles | PASS |
| RF-03 | Creación de un curso como docente | PASS |
| RF-04 | Consulta de cursos del docente | PASS |
| RF-05 | Modificación de un curso | PASS |
| RF-06 | Creación de una sesión académica | PASS |
| RF-07 | Consulta de sesiones académicas | PASS |
| RF-08 | Generación de un código QR | PASS |
| RF-09 | Registro de asistencia como estudiante | PASS |
| RF-10 | Consulta de asistencias como docente | PASS |

## 5. Resumen de resultados

Se evaluaron los diez requisitos funcionales definidos para el proyecto.

- Total de requisitos evaluados: 10.
- Pruebas aprobadas: 10.
- Pruebas fallidas: 0.
- Porcentaje de aprobación: 100 %.

Los resultados corresponden a las pruebas manuales reportadas durante la validación del sistema integrado.

## 6. Pruebas automatizadas del frontend

Se ejecutaron los siguientes comandos:

- npm test
- npm run lint
- npm run build

Resultados obtenidos:

| Verificación | Resultado |
|---|---|
| Pruebas automatizadas | 27 de 27 aprobadas |
| Archivos de pruebas | 4 de 4 aprobados |
| Análisis estático con Oxlint | 0 errores y 1 advertencia |
| Compilación del frontend | Exitosa |

Durante la integración se identificaron siete pruebas fallidas en el módulo de inscripciones debido a expectativas desactualizadas respecto a los textos de la interfaz.

Después de actualizar las pruebas y aislar la dependencia del historial de asistencias, se ejecutó nuevamente la suite y se obtuvieron 27 pruebas aprobadas.

## 7. Verificación de infraestructura

Se utilizó Docker Compose para iniciar los componentes del sistema.

| Componente | Resultado |
|---|---|
| PostgreSQL 17 | En ejecución y saludable |
| API Spring Boot | En ejecución |
| Portal React y Nginx | En ejecución |
| Endpoint /health | HTTP 200 OK |
| Compilación de imágenes Docker | Exitosa |

Estas verificaciones confirman el funcionamiento inicial de la infraestructura local.

## 8. Observaciones

Durante el análisis del frontend se identificaron dos advertencias:

1. Oxlint detectó una actualización de estado dentro de un efecto de React en el archivo App.tsx.
2. Vite advirtió que uno de los archivos JavaScript generados supera los 500 kB.

Estas advertencias no impidieron completar las pruebas ni la compilación del portal.

Se recomienda revisarlas posteriormente como posibles oportunidades de mejora.

## 9. Limitaciones

Los resultados presentados corresponden a las verificaciones funcionales manuales, las pruebas automatizadas del frontend y la comprobación inicial de infraestructura.

Aún están pendientes:

- Ejecución y documentación de las pruebas automatizadas del backend.
- Medición de complejidad ciclomática.
- Medición de líneas de código.
- Aplicación de la suite de métricas CK.
- Validación completa de los requisitos no funcionales.
- Comparación de resultados antes y después de las refactorizaciones.

## 10. Evidencias

Las capturas de pantalla y los registros de ejecución se almacenarán en la carpeta docs/evidencias/.

Las evidencias deben identificar la prueba realizada y su resultado, evitando incluir contraseñas, tokens de autenticación o información sensible.

## 11. Conclusión

La validación funcional manual de Attendance Registry permitió comprobar satisfactoriamente los diez requisitos funcionales establecidos para el proyecto.

Las pruebas automatizadas del frontend también finalizaron correctamente, con 27 pruebas aprobadas, mientras que la infraestructura local permitió iniciar los tres servicios principales del sistema.

Estos resultados constituyen la base funcional para las siguientes actividades de análisis de mantenibilidad, medición de métricas y refactorización del código.
