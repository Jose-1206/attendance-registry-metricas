# Attendance Registry — Métricas y Estándares de Calidad

## 1. Descripción del proyecto

**Attendance Registry**, también conocido como **Att-book**, es un sistema de información para la gestión de asistencia académica.

El sistema busca facilitar el registro, seguimiento y consulta de la asistencia de estudiantes mediante sesiones académicas y códigos QR.

Este proyecto se desarrolla para la asignatura **Métricas y Estándares de Calidad** de la Corporación Universitaria del Huila (CORHUILA).

Se reutilizará como base el proyecto Attendance Registry desarrollado en la asignatura de Fullstack, realizando una adaptación independiente para evaluar su mantenibilidad, identificar problemas en el código y proponer mejoras respaldadas por métricas de calidad.

## 2. Objetivo general

Evaluar y mejorar la mantenibilidad del sistema Attendance Registry mediante la aplicación de métricas de calidad de software, identificando problemas de complejidad, tamaño y diseño que permitan proponer refactorizaciones justificadas.

### Objetivos específicos

1. Definir diez requisitos funcionales y tres requisitos no funcionales medibles.
2. Documentar y justificar la arquitectura de software utilizada.
3. Verificar el funcionamiento de los módulos principales mediante pruebas.
4. Aplicar métricas de complejidad ciclomática, líneas de código y diseño orientado a objetos.
5. Identificar los componentes que superen los umbrales de calidad establecidos.
6. Proponer mejoras y evaluar su impacto mediante nuevas mediciones.

## 3. Integrantes del equipo

| Integrante | Responsabilidad |
|---|---|
| José Miguel Aya Peralta | Gestión del repositorio, requisitos, integración del portal y documentación |
| Luis Fernando Perez Chala | Arquitectura, base de datos, implementación de sesiones y medición de calidad |
| Andrés Felipe Calle Gonzalez | Gestión de asistencias, códigos QR, pruebas y refactorización |

Los usuarios de GitHub de cada integrante se registrarán después de verificar sus cuentas.

## 4. Requisitos funcionales

Se han seleccionado los siguientes requisitos para el alcance del proyecto:

| Código | Requisito |
|---|---|
| RF-01 | Registrar estudiantes en el sistema |
| RF-02 | Autenticar usuarios y controlar el acceso según su rol |
| RF-03 | Crear cursos académicos |
| RF-04 | Consultar los cursos asociados a un docente |
| RF-05 | Actualizar la información de un curso |
| RF-06 | Crear sesiones académicas |
| RF-07 | Consultar las sesiones asociadas a un curso |
| RF-08 | Generar códigos QR temporales para registrar asistencia |
| RF-09 | Registrar la asistencia mediante un código QR válido |
| RF-10 | Consultar reportes de asistencia por sesión académica |

Estos requisitos forman parte del alcance propuesto. Algunos ya existen en el proyecto original y otros se encuentran pendientes de implementación.

Los criterios de aceptación, estados y pruebas se documentarán en `docs/requisitos/requisitos.md`.

## 5. Requisitos no funcionales

| Código | Categoría | Criterio propuesto |
|---|---|---|
| RNF-01 | Mantenibilidad | Los métodos del backend deben tener una complejidad ciclomática menor o igual a 10 en el alcance final definido |
| RNF-02 | Seguridad | El 100 % de los endpoints protegidos debe rechazar accesos sin autorización según su política de seguridad |
| RNF-03 | Rendimiento | El percentil 95 de las respuestas de los endpoints críticos debe ser menor o igual a 2 segundos bajo una carga de prueba documentada |

El cumplimiento de estos criterios se determinará mediante mediciones y pruebas reproducibles.

## 6. Arquitectura del sistema

La arquitectura preliminar está basada en un backend organizado por capas y un portal web que consume sus servicios mediante una API REST.

Se identifican los siguientes componentes:

- **Presentación:** interfaz web para docentes y estudiantes.
- **Controladores:** recepción y validación de solicitudes HTTP.
- **Servicios:** lógica de negocio y reglas del sistema.
- **Persistencia:** repositorios y entidades para el almacenamiento de información.
- **Base de datos:** almacenamiento persistente de usuarios, cursos, sesiones y asistencias.

La arquitectura definitiva, su justificación y sus diagramas se documentarán en `docs/arquitectura/arquitectura.md`.

## 7. Tecnologías

El proyecto original utiliza las siguientes tecnologías:

| Componente | Tecnología |
|---|---|
| Backend | Java y Spring Boot |
| Frontend | React y TypeScript |
| Base de datos | PostgreSQL |
| Control de versiones | Git y GitHub |
| Pruebas backend | JUnit |
| Pruebas frontend | Vitest |
| Construcción backend | Maven |
| Construcción frontend | Node.js y npm |

Se confirmará con el docente el alcance de los lenguajes de programación que se conservarán para esta asignatura.

## 8. Métricas de calidad

Se utilizarán tres grupos de métricas de mantenibilidad:

### 8.1. Complejidad ciclomática — V(G)

Permite analizar la cantidad de caminos lógicamente independientes dentro de un método.

Umbrales de referencia:

- 1 a 10: riesgo bajo.
- 11 a 20: riesgo moderado.
- 21 a 50: riesgo alto.
- Más de 50: riesgo muy alto.

### 8.2. Líneas de código — LOC

Permite evaluar el tamaño de los componentes y métodos.

Se analizarán las líneas físicas, efectivas, en blanco y los comentarios.

Como referencia, los métodos con más de 50 líneas efectivas requerirán revisión.

### 8.3. Suite CK

Se analizarán métricas de diseño orientado a objetos, incluyendo:

- WMC: complejidad ponderada de los métodos.
- DIT: profundidad del árbol de herencia.
- NOC: número de clases hijas.
- CBO: acoplamiento entre clases.
- RFC: respuesta potencial de una clase.
- LCOM: cohesión de los métodos.

La herramienta, la fórmula o implementación utilizada, los umbrales y los resultados reales se documentarán en `docs/metricas/`.

## 9. Estrategia de evaluación

El proceso de calidad seguirá las siguientes etapas:

1. Establecer una versión inicial del código.
2. Ejecutar las pruebas disponibles.
3. Aplicar métricas de mantenibilidad.
4. Comparar los resultados con los umbrales definidos.
5. Identificar los componentes con posibles problemas.
6. Priorizar mejoras según su riesgo y esfuerzo.
7. Implementar al menos una refactorización, cuando sea viable.
8. Ejecutar nuevamente las pruebas y métricas.
9. Comparar los resultados antes y después.

Las mediciones deberán conservarse como evidencia y podrán reproducirse utilizando los comandos documentados.

## 10. Pruebas

Se utilizarán pruebas para verificar el comportamiento de los requisitos principales.

Se contemplan:

- Pruebas unitarias.
- Pruebas de integración.
- Pruebas de autenticación y autorización.
- Pruebas funcionales.
- Pruebas de regresión después de las refactorizaciones.

Las evidencias estarán disponibles en `docs/pruebas/` y `docs/evidencias/`.

## 11. Instalación y ejecución

Las instrucciones de instalación se completarán después de integrar y validar el código del proyecto.

Se documentarán:

1. Herramientas y versiones necesarias.
2. Configuración de variables de entorno.
3. Preparación de la base de datos.
4. Ejecución del backend.
5. Ejecución del portal.
6. Comandos para ejecutar pruebas.
7. Comandos para calcular las métricas.

No se incluirán contraseñas ni claves privadas en el repositorio.

## 12. Organización del repositorio

La estructura inicial es:

```text
attendance-registry-metricas/
├── README.md
├── docs/
│   ├── requisitos/
│   │   └── requisitos.md
│   ├── arquitectura/
│   │   └── arquitectura.md
│   ├── metricas/
│   │   └── definicion-metricas.md
│   ├── pruebas/
│   └── evidencias/
└── .gitignore
```

Posteriormente se incorporará el código fuente de la aplicación y las herramientas de medición.

## 13. Mejoras propuestas

Las mejoras se definirán a partir de los resultados reales de las métricas.

Entre las técnicas que podrán evaluarse se encuentran:

- Extraer métodos para reducir la complejidad.
- Dividir clases con múltiples responsabilidades.
- Reducir el acoplamiento entre componentes.
- Mejorar la organización de servicios.
- Eliminar duplicaciones innecesarias.
- Incrementar las pruebas de regresión.

Las propuestas no se presentarán como defectos confirmados hasta contar con evidencia.

## 14. Estado del proyecto

**Estado:** En desarrollo.

Actualmente se está realizando la configuración inicial, la definición de requisitos y la preparación de la documentación.

Los resultados de las métricas y la evaluación final se incorporarán cuando se complete el análisis del código.

## 15. Informe final

El informe académico estará disponible en la carpeta `docs/` una vez finalizado.

Contendrá los requisitos, la arquitectura, las métricas, los resultados, el análisis, las mejoras y los aportes individuales.

## 16. Origen y licencia

El proyecto se basa en Attendance Registry (Att-book), desarrollado originalmente en la asignatura de Fullstack.

Antes de incorporar el código original se verificará la autorización para reutilizarlo y se conservará el reconocimiento de sus autores.

La licencia de esta adaptación se definirá de acuerdo con los permisos del proyecto original.

## 17. Contexto académico

**Institución:** Corporación Universitaria del Huila — CORHUILA

**Asignatura:** Métricas y Estándares de Calidad

**Docente:** José Miguel Llanos Mosquera

**Proyecto:** Evaluación de mantenibilidad y evolución de Attendance Registry