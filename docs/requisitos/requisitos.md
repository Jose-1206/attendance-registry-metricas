# Requisitos del sistema — Attendance Registry

## 1. Introducción

Este documento define los requisitos funcionales y no funcionales del sistema Attendance Registry, también conocido como Att-book, para el proyecto final de la asignatura Métricas y Estándares de Calidad.

Los requisitos describen el comportamiento esperado del sistema y establecen criterios verificables para evaluar su cumplimiento.

Se utiliza como referencia la implementación de Attendance Registry desarrollada en la asignatura Fullstack.

## 2. Descripción del problema

En el entorno académico, el registro manual de asistencia puede ocasionar errores, pérdida de información, duplicación de registros y dificultades para consultar el historial de los estudiantes.

Attendance Registry busca facilitar este proceso mediante una plataforma que permita a los docentes administrar cursos y sesiones académicas, generar códigos QR y consultar reportes de asistencia.

Los estudiantes podrán autenticarse y registrar su asistencia mediante códigos QR válidos.

## 3. Usuarios del sistema

### 3.1. Docente

Usuario responsable de administrar cursos y sesiones académicas.

Sus principales funciones son:

- Crear, consultar y actualizar cursos.
- Crear y consultar sesiones académicas.
- Generar códigos QR de asistencia.
- Consultar los reportes de asistencia de sus sesiones.

### 3.2. Estudiante

Usuario que participa en los cursos académicos.

Sus principales funciones son:

- Registrarse en el sistema.
- Iniciar sesión.
- Registrar su asistencia mediante un código QR válido.

## 4. Alcance del proyecto

El proyecto contempla la implementación y evaluación de las funcionalidades principales de Attendance Registry.

Se incluyen:

- Autenticación y autorización.
- Administración de cursos.
- Gestión de sesiones académicas.
- Generación de códigos QR temporales.
- Registro y validación de asistencias.
- Consulta de reportes.
- Pruebas funcionales.
- Evaluación de mantenibilidad del código.

Quedan fuera del alcance inicial las notificaciones automáticas, la recuperación de contraseñas y las integraciones con plataformas académicas externas.

## 5. Requisitos funcionales

### RF-01. Registro de estudiantes

**Descripción:** El sistema debe permitir registrar estudiantes mediante sus datos personales y credenciales de acceso.

**Actor:** Estudiante.

**Criterios de aceptación:**

- Un estudiante con datos válidos puede registrarse correctamente.
- El sistema impide registrar dos cuentas con el mismo correo electrónico.
- Los datos obligatorios son validados.
- La contraseña no se almacena en texto plano.
- El estudiante registrado puede autenticarse posteriormente.

**Estado preliminar:** Implementado en la API. Pendiente verificar el flujo completo desde el portal.

### RF-02. Autenticación y control de acceso

**Descripción:** El sistema debe permitir el inicio de sesión y restringir el acceso a las funcionalidades según el rol del usuario.

**Actores:** Docente y estudiante.

**Criterios de aceptación:**

- Un usuario con credenciales válidas puede iniciar sesión.
- Las credenciales incorrectas son rechazadas.
- Las solicitudes a recursos protegidos requieren autenticación.
- Un estudiante no puede ejecutar operaciones reservadas a docentes.
- Un docente únicamente puede administrar los recursos que le corresponden, cuando aplique.

**Estado preliminar:** Implementado en la API y el portal para las funcionalidades existentes.

### RF-03. Creación de cursos

**Descripción:** El sistema debe permitir a los docentes registrar nuevos cursos académicos.

**Actor:** Docente.

**Criterios de aceptación:**

- El docente puede crear un curso con información válida.
- El sistema valida los campos obligatorios.
- El curso queda asociado al docente que lo creó.
- Los estudiantes no pueden crear cursos.
- La información creada puede consultarse posteriormente.

**Estado preliminar:** Implementado.

### RF-04. Consulta de cursos

**Descripción:** El sistema debe permitir a los docentes consultar los cursos académicos que administran.

**Actor:** Docente.

**Criterios de aceptación:**

- El docente autenticado puede obtener sus cursos.
- La consulta devuelve la información correspondiente a cada curso.
- La lista no incluye cursos pertenecientes a otros docentes.
- Si el docente no tiene cursos, el sistema muestra una lista vacía sin producir errores.

**Estado preliminar:** Implementado.

### RF-05. Actualización de cursos

**Descripción:** El sistema debe permitir a los docentes modificar la información de sus cursos.

**Actor:** Docente.

**Criterios de aceptación:**

- El docente puede actualizar un curso propio utilizando datos válidos.
- Los cambios se conservan al consultar nuevamente el curso.
- Un docente no puede modificar cursos de otros docentes.
- Las solicitudes con información inválida son rechazadas.

**Estado preliminar:** Implementado.

### RF-06. Creación de sesiones académicas

**Descripción:** El sistema debe permitir a los docentes crear sesiones académicas vinculadas a un curso existente.

**Actor:** Docente.

**Criterios de aceptación:**

- Un docente puede crear una sesión para un curso propio.
- La sesión queda relacionada con el curso correspondiente.
- El sistema valida los datos obligatorios.
- No se permite crear sesiones en cursos ajenos.
- La sesión creada puede consultarse posteriormente.

**Estado preliminar:** Pendiente de implementación según la copia revisada.

### RF-07. Consulta de sesiones académicas

**Descripción:** El sistema debe permitir consultar las sesiones académicas asociadas a un curso.

**Actor:** Docente.

**Criterios de aceptación:**

- El docente puede consultar las sesiones de un curso propio.
- Cada sesión presenta la información necesaria para identificarla.
- El sistema no mezcla sesiones pertenecientes a cursos diferentes.
- Los accesos no autorizados son rechazados.

**Estado preliminar:** Pendiente de implementación.

### RF-08. Generación de códigos QR temporales

**Descripción:** El sistema debe permitir a los docentes generar códigos QR temporales asociados a una sesión académica.

**Actor:** Docente.

**Criterios de aceptación:**

- El docente puede generar un código QR para una sesión propia.
- El código generado se relaciona con una sesión específica.
- El código tiene una fecha u hora de vencimiento.
- Un código vencido no puede utilizarse para registrar asistencia.
- Los usuarios no autorizados no pueden generar códigos QR para sesiones ajenas.

**Estado preliminar:** Pendiente de implementación.

### RF-09. Registro de asistencia mediante QR

**Descripción:** El sistema debe permitir a los estudiantes registrar su asistencia utilizando un código QR válido.

**Actor:** Estudiante.

**Criterios de aceptación:**

- Un estudiante autenticado puede registrar asistencia con un código válido.
- El registro queda relacionado con el estudiante y la sesión.
- Un código QR vencido es rechazado.
- No se permite registrar dos asistencias del mismo estudiante en una misma sesión.
- Los códigos inexistentes o inválidos son rechazados.
- El sistema confirma el resultado del intento de registro.

**Estado preliminar:** Pendiente de implementación.

### RF-10. Consulta de reportes de asistencia

**Descripción:** El sistema debe permitir a los docentes consultar los registros de asistencia correspondientes a sus sesiones académicas.

**Actor:** Docente.

**Criterios de aceptación:**

- El docente puede consultar los registros de una sesión propia.
- El reporte identifica los estudiantes que registraron asistencia.
- Los registros corresponden a la sesión solicitada.
- Un docente no puede consultar reportes de sesiones pertenecientes a otros docentes.
- Cuando no existen asistencias, el sistema devuelve un resultado vacío válido.

**Estado preliminar:** Pendiente de implementación.

## 6. Requisitos no funcionales

### RNF-01. Mantenibilidad

**Categoría:** Mantenibilidad.

**Descripción:** Los métodos del backend incluidos en el alcance final de evaluación deberán presentar una complejidad ciclomática menor o igual a 10.

**Criterio de aceptación:** El 100 % de los métodos incluidos en el alcance final acordado debe cumplir V(G) ≤ 10.

**Forma de verificación:** Aplicar una herramienta de análisis estático que permita medir la complejidad ciclomática de los métodos del backend.

**Evidencia:** Tabla de complejidad por método y registro de resultados antes y después de las mejoras.

### RNF-02. Seguridad

**Categoría:** Seguridad.

**Descripción:** El sistema debe proteger los recursos restringidos mediante mecanismos de autenticación y autorización.

**Criterio de aceptación:** El 100 % de los endpoints identificados como protegidos debe rechazar solicitudes sin autenticación válida y solicitudes que incumplan las políticas de rol o propiedad.

**Forma de verificación:** Ejecutar pruebas automatizadas de seguridad para los endpoints protegidos, incluyendo solicitudes anónimas, credenciales inválidas y usuarios sin permisos.

**Evidencia:** Resultados de pruebas de autenticación y autorización.

### RNF-03. Rendimiento

**Categoría:** Rendimiento.

**Descripción:** Los endpoints principales del sistema deberán responder en un tiempo adecuado durante las operaciones habituales.

**Criterio de aceptación:** El percentil 95 de los tiempos de respuesta deberá ser menor o igual a 2.000 milisegundos.

**Condiciones propuestas:** Ejecutar una prueba controlada sobre los endpoints críticos con 20 usuarios concurrentes y al menos 100 solicitudes por endpoint, registrando el hardware y el entorno de ejecución.

**Forma de verificación:** Utilizar una herramienta de pruebas de rendimiento para registrar los tiempos de respuesta.

**Evidencia:** Resultados de la prueba de carga, percentiles y descripción del entorno.

## 7. Matriz de trazabilidad preliminar

| Requisito | Componente previsto o existente | Prueba requerida | Estado |
|---|---|---|---|
| RF-01 | AuthController / IdentityService | Registro válido, duplicado y datos inválidos | API implementada |
| RF-02 | AuthController / SecurityConfig | Autenticación y autorización por rol | Implementado |
| RF-03 | CourseController / CourseService | Creación y validación de curso | Implementado |
| RF-04 | CourseController / CourseService | Consulta filtrada por docente | Implementado |
| RF-05 | CourseController / CourseService | Actualización y autorización | Implementado |
| RF-06 | Módulo de sesiones por desarrollar | Creación y vinculación con curso | Pendiente |
| RF-07 | Módulo de sesiones por desarrollar | Consulta de sesiones y autorización | Pendiente |
| RF-08 | Módulo QR por desarrollar | Generación, asociación y expiración | Pendiente |
| RF-09 | Módulo de asistencias por desarrollar | Registro, duplicados y QR vencido | Pendiente |
| RF-10 | Módulo de reportes por desarrollar | Consulta y restricción por propietario | Pendiente |
| RNF-01 | Backend Java | Medición V(G) | Sin medición actual |
| RNF-02 | Configuración de seguridad y endpoints | Pruebas de acceso | Verificación pendiente |
| RNF-03 | API REST | Pruebas de rendimiento | Verificación pendiente |

Los componentes pendientes serán actualizados cuando se implemente el código correspondiente.

## 8. Verificación del cumplimiento

Cada requisito funcional se considerará cumplido cuando:

1. La funcionalidad correspondiente esté implementada.
2. Se satisfagan todos sus criterios de aceptación.
3. Exista evidencia de las pruebas correspondientes.
4. Se actualice su trazabilidad con los componentes reales.

Los requisitos no funcionales se considerarán cumplidos únicamente cuando las mediciones realizadas alcancen los criterios establecidos.

## 9. Relación con el proyecto de Métricas

La implementación de estos requisitos permitirá analizar la calidad interna del sistema mediante tres grupos principales de métricas:

- Complejidad ciclomática.
- Líneas de código.
- Suite CK para diseño orientado a objetos.

Los resultados permitirán detectar problemas de mantenibilidad, priorizar mejoras y evaluar el efecto de posibles refactorizaciones.

## 10. Estado del documento

**Versión:** 1.0 — Definición inicial.

**Responsable:** José Miguel Aya Peralta.

**Estado:** Documento preliminar. Los requisitos y criterios deberán verificarse contra la implementación final, los resultados de pruebas y la aprobación del docente.

**Nota:** Los estados de implementación se basan en la copia de Attendance Registry analizada para este proyecto y no sustituyen una nueva verificación del código integrado.