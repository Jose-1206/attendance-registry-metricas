# Definición de métricas de calidad — Attendance Registry

## 1. Introducción

Este documento describe las métricas seleccionadas para evaluar la mantenibilidad y evolución del sistema Attendance Registry (Att-book).

El propósito es analizar la calidad interna del código, identificar posibles problemas de diseño y proponer refactorizaciones fundamentadas en resultados cuantificables.

Las métricas seleccionadas corresponden a los criterios establecidos en el proyecto final de la asignatura Métricas y Estándares de Calidad.

## 2. Objetivos de la medición

**Objetivo general**

Evaluar la mantenibilidad del sistema Attendance Registry mediante métricas de complejidad, tamaño y diseño orientado a objetos.

**Objetivos específicos**

1. Identificar métodos con complejidad ciclomática elevada.
2. Detectar métodos con una cantidad excesiva de líneas de código.
3. Evaluar el acoplamiento, la cohesión y la complejidad de las clases.
4. Comparar los resultados con los umbrales de referencia.
5. Proponer mejoras y refactorizaciones justificadas.
6. Evaluar los cambios obtenidos mediante nuevas mediciones.

## 3. Alcance de la medición

Las métricas principales se aplicarán sobre el backend Java de Attendance Registry.

Se analizarán los métodos y clases correspondientes a:

- Autenticación y usuarios.
- Gestión de cursos.
- Gestión de sesiones académicas.
- Generación y validación de códigos QR.
- Registro y consulta de asistencias.

Los módulos pendientes serán incluidos cuando se implementen.

El backend se selecciona como núcleo principal de medición porque contiene las reglas de negocio y utiliza programación orientada a objetos.

La aplicación también cuenta con un portal desarrollado en React y TypeScript. Su exclusión de las métricas principales deberá justificarse y acordarse con el docente. Podrán incorporarse métricas complementarias para el portal.

## 4. Métrica 1: Complejidad ciclomática

### 4.1. Nombre

Complejidad ciclomática de McCabe — V(G).

### 4.2. Atributo que mide

La complejidad del flujo de control de los métodos del sistema.

Permite estimar la cantidad de caminos de ejecución linealmente independientes que existen dentro de un método.

### 4.3. Fórmula

La expresión general es:

**V(G) = E − N + 2P**

Donde:

- E: número de aristas del grafo de flujo de control.
- N: número de nodos del grafo.
- P: número de componentes conectados.

Para el análisis simplificado de un método con un único componente se puede utilizar:

**V(G) = D + 1**

Donde D representa los puntos de decisión según las reglas de conteo de la herramienta utilizada.

La herramienta y sus convenciones se documentarán para que la medición sea reproducible.

### 4.4. Umbrales de referencia

| Valor | Interpretación |
|---|---|
| 1–10 | Riesgo bajo |
| 11–20 | Riesgo moderado |
| 21–50 | Riesgo alto |
| Más de 50 | Riesgo muy alto |

### 4.5. Herramienta

Se evaluará una herramienta de análisis estático para Java que proporcione complejidad ciclomática por método.

Herramienta y versión definitiva: pendientes de validación.

No se utilizará WMC como sustituto automático de V(G) por método: se comprobará qué calcula exactamente la herramienta seleccionada.

### 4.6. Alcance

Métodos de las clases Java de los módulos principales del backend.

### 4.7. Justificación

Esta métrica permite identificar métodos que pueden resultar difíciles de comprender, modificar y probar debido a la cantidad de decisiones que contienen.

Una complejidad elevada puede requerir un mayor número de casos de prueba.

### 4.8. Acciones propuestas

Cuando un método supere los umbrales establecidos se evaluarán técnicas como:

- Extract Method: dividir el método en operaciones con responsabilidades claras.
- Simplificación de condiciones.
- Eliminación de decisiones innecesarias.
- Separación de responsabilidades.

Las propuestas se seleccionarán después de revisar los resultados reales.

## 5. Métrica 2: Líneas de código

### 5.1. Nombre

Líneas de código — LOC.

### 5.2. Atributo que mide

El tamaño del código fuente.

Permite identificar componentes extensos que podrían concentrar responsabilidades excesivas.

### 5.3. Forma de cálculo

Se analizarán:

**LOC físicas:** cantidad total de líneas del archivo.

**LOC efectivas:** líneas que contienen código fuente ejecutable o declaraciones, según la definición de la herramienta.

**Líneas de comentarios:** líneas utilizadas para documentar el código.

**Líneas en blanco:** líneas sin contenido.

Para obtener el porcentaje de comentarios se utilizará:

**Porcentaje de comentarios = (líneas de comentario / líneas totales) × 100**

En los casos de comentarios y código en la misma línea se documentará la regla de conteo utilizada.

### 5.4. Umbral de referencia

Según la guía del proyecto:

**Más de 50 líneas efectivas por método constituye una señal de alerta.**

Este umbral no significa automáticamente que el código sea incorrecto.

### 5.5. Herramienta

Se utilizará un analizador o script capaz de contar las líneas del código Java.

La herramienta, su versión y sus criterios de clasificación de líneas serán registrados al realizar las mediciones.

### 5.6. Alcance

Clases y métodos de los módulos principales del backend.

### 5.7. Justificación

Los métodos extensos pueden dificultar la comprensión del código y aumentar el esfuerzo de mantenimiento.

Esta métrica se interpretará junto con la complejidad y las responsabilidades del componente.

### 5.8. Acciones propuestas

Cuando se encuentren métodos extensos se evaluará:

- División de métodos.
- Extracción de responsabilidades.
- Eliminación de código duplicado.
- Simplificación de operaciones.

## 6. Métrica 3: Suite CK

### 6.1. Nombre

Suite de métricas Chidamber y Kemerer — CK.

### 6.2. Atributo que mide

Características del diseño orientado a objetos, como complejidad, herencia, acoplamiento y cohesión.

### 6.3. Métricas seleccionadas

**WMC — Weighted Methods per Class**

Evalúa la complejidad ponderada de los métodos de una clase.

En su definición general:

WMC = suma de las complejidades asignadas a los métodos de la clase.

La implementación exacta dependerá de la herramienta utilizada.

**DIT — Depth of Inheritance Tree**

Representa la profundidad de una clase dentro de su jerarquía de herencia.

**NOC — Number of Children**

Indica el número de clases hijas directas dentro de una jerarquía.

**CBO — Coupling Between Objects**

Evalúa el acoplamiento entre clases.

Un valor elevado puede indicar que una clase depende de numerosas clases externas.

**RFC — Response for a Class**

Estima el conjunto de métodos que potencialmente pueden ejecutarse como respuesta a un mensaje recibido por un objeto.

**LCOM — Lack of Cohesion in Methods**

Evalúa la falta de cohesión entre los métodos de una clase.

Existen distintas variantes de LCOM. Para la interpretación mediante un umbral normalizado se documentará explícitamente si se utiliza LCOM*.

### 6.4. Umbrales de referencia

| Métrica | Señal de alerta |
|---|---|
| WMC | Mayor que 20 |
| DIT | Mayor que 5 |
| CBO | Mayor que 10 |
| RFC | Mayor que 50 |
| LCOM normalizado | Mayor que 0,7 |

La guía no establece un umbral numérico para NOC, por lo que su valor se analizará en el contexto del diseño.

Los resultados de LCOM solo se compararán con el umbral de 0,7 si se utiliza una variante normalizada compatible.

### 6.5. Herramienta

Se propone utilizar CK para Java.

La versión, el comando de ejecución, los parámetros y los archivos de resultados se registrarán durante la medición.

### 6.6. Alcance

Clases Java correspondientes a los módulos principales del backend.

### 6.7. Justificación

La suite CK permite identificar riesgos asociados al diseño orientado a objetos.

Sus resultados pueden facilitar la detección de clases con demasiadas responsabilidades, acoplamiento elevado o cohesión insuficiente.

### 6.8. Acciones propuestas

Cuando los valores indiquen problemas de diseño se evaluarán:

- Extract Class.
- Reducción de dependencias.
- Separación de responsabilidades.
- Introducción de interfaces cuando aporte beneficios.
- Reorganización de servicios.

## 7. Procedimiento de medición

La medición seguirá este procedimiento:

1. Obtener una versión identificable del código fuente.
2. Registrar el identificador del commit analizado.
3. Registrar las versiones de Java y de las herramientas.
4. Ejecutar las pruebas correspondientes.
5. Aplicar las herramientas de medición.
6. Conservar los resultados originales.
7. Comparar los valores con los umbrales establecidos.
8. Identificar los componentes que requieren revisión.
9. Proponer y, cuando proceda, implementar mejoras.
10. Repetir las mediciones bajo condiciones equivalentes.

Se diferenciará la medición inicial del proyecto reutilizado, la medición del sistema funcional y la medición posterior a la refactorización.

## 8. Registro de resultados

Los datos obtenidos se organizarán de acuerdo con el Anexo C de la guía del proyecto.

| Elemento medido | Métrica | Valor obtenido | Umbral | Estado / Acción |
|---|---|---|---|---|
| Pendiente | V(G) | Sin medir | ≤ 10 | Pendiente |
| Pendiente | LOC efectivas por método | Sin medir | ≤ 50 | Pendiente |
| Pendiente | WMC | Sin medir | ≤ 20 | Pendiente |
| Pendiente | CBO | Sin medir | ≤ 10 | Pendiente |
| Pendiente | RFC | Sin medir | ≤ 50 | Pendiente |
| Pendiente | LCOM* | Sin medir | ≤ 0,7 | Pendiente |

Los valores se completarán exclusivamente con resultados obtenidos mediante herramientas ejecutadas sobre el código real.

## 9. Comparación antes y después

Cuando se realice una refactorización se registrará:

| Elemento | Métrica | Antes | Después | Interpretación |
|---|---|---|---|---|
| Pendiente | V(G) | Sin medir | Sin medir | Pendiente |
| Pendiente | LOC | Sin medir | Sin medir | Pendiente |
| Pendiente | CK | Sin medir | Sin medir | Pendiente |

La comparación se realizará utilizando la misma herramienta, versión y configuración, o explicando las diferencias cuando no sea posible mantenerlas.

También se ejecutarán pruebas de regresión para verificar que la refactorización no modifique el comportamiento esperado.

## 10. Responsabilidades

**José Miguel Aya Peralta:** organización de la documentación, trazabilidad e integración de evidencias.

**Luis Fernando Perez Chala:** validación de las herramientas, configuración de las mediciones y análisis inicial de resultados.

**Andrés Felipe Calle Gonzalez:** identificación de problemas, pruebas de regresión, propuestas de refactorización y comparación de resultados.

Los tres integrantes participarán en la interpretación de las métricas y en la sustentación.

## 11. Estado del documento

**Versión:** 1.0 — Definición inicial.

**Preparación inicial:** José Miguel Aya Peralta.

**Responsable de validación técnica:** Luis Fernando Perez Chala.

**Estado:** Pendiente de ejecutar las herramientas y obtener mediciones reales.

## 12. Referencias

- Llanos Mosquera, J. M. *Proyecto Final: Métricas de Mantenibilidad y Evolución aplicadas a una Aplicación Real*. Guía de la asignatura Métricas y Estándares de Calidad.
- McCabe, T. J. (1976). *A Complexity Measure*. IEEE Transactions on Software Engineering.
- Chidamber, S. R., & Kemerer, C. F. (1994). *A Metrics Suite for Object Oriented Design*. IEEE Transactions on Software Engineering.

Las referencias a herramientas y versiones específicas se incorporarán una vez seleccionadas y verificadas.