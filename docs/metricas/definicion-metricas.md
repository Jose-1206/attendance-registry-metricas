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

Se utilizó **PMD 7.17.0**, integrado mediante `maven-pmd-plugin` versión 3.28.0, para evaluar la complejidad ciclomática del backend Java 17.

La configuración se encuentra en `sistema/att-book-api/config/pmd-complejidad.xml`.

El análisis inicial identificó tres infracciones del umbral V(G) ≤ 10. Después de la refactorización, PMD registró cero infracciones.

Las evidencias se conservan en `docs/metricas/pmd-final/` y `docs/metricas/pmd-post-refactor/`.

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

Para medir las líneas de código del backend se utilizó **CLOC 2.08**, aplicado a los 43 archivos Java de producción.

Se obtuvieron 2.017 líneas de código, 31 líneas de comentarios y 489 líneas en blanco, para un total de 2.537 líneas físicas.

Para analizar la longitud de los métodos se utilizó la columna `loc` del archivo `docs/metricas/ck-post-refactor/method.csv`. Se revisaron 336 registros de métodos y constructores, y no se encontraron métodos no constructores que superaran 50 LOC.

Las evidencias se encuentran en `docs/metricas/loc-final/`.

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

Se utilizó la herramienta **CK de Mauricio Aniche** para calcular métricas de diseño orientado a objetos del backend Java.

Se analizaron 48 clases e interfaces de producción en las mediciones comparables anteriores y posteriores a la refactorización.

Los resultados se conservan en los directorios `docs/metricas/ck-inicial/`, `docs/metricas/ck-final/` y `docs/metricas/ck-post-refactor/`.

La versión exacta de CK debe registrarse a partir del archivo ejecutable utilizado para realizar las mediciones.

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

Las mediciones se realizaron sobre el backend Java del sistema Attendance Registry.

| Elemento medido | Métrica | Valor obtenido | Umbral | Estado / Acción |
|---|---|---|---|---|
| Backend después de la refactorización | V(G) | 0 infracciones PMD | ≤ 10 por método | Cumple según PMD |
| Métodos Java no constructores | LOC por método | 0 métodos con más de 50 LOC | ≤ 50 | Sin alertas según CK |
| SessionService | WMC | 44 | ≤ 20 | Revisar responsabilidades |
| AttendanceService | WMC | 24 | ≤ 20 | Revisar complejidad agregada |
| CourseController | CBO | 19 | ≤ 10 | Revisar dependencias |
| SessionController | CBO | 19 | ≤ 10 | Revisar dependencias |
| SessionService | LCOM* | 0,796 | ≤ 0,7 | Revisar cohesión |
| AttendanceService | LCOM* | 0,764 | ≤ 0,7 | Revisar cohesión |

### 8.1. Resultados globales de LOC

La medición se realizó utilizando CLOC versión 2.08 sobre 43 archivos Java de producción.

| Indicador | Resultado |
|---|---:|
| Líneas físicas | 2.537 |
| Líneas de código | 2.017 |
| Líneas de comentarios | 31 |
| Líneas en blanco | 489 |
| Porcentaje de comentarios | 1,22 % |

El conteo por método se obtuvo del archivo `ck-post-refactor/method.csv`, que contiene 336 registros de métodos y constructores.

Tras excluir los constructores, no se encontraron métodos con LOC superior a 50.

Los resultados reflejan el alcance y los criterios de las herramientas utilizadas.

### 8.2. Interpretación

La evaluación mediante PMD indica que se eliminó la totalidad de las infracciones del umbral de complejidad ciclomática detectadas previamente.

La medición de LOC por método no identificó valores superiores al límite de referencia.

Sin embargo, CK permitió identificar clases con valores elevados de WMC, CBO y LCOM*, que requieren atención desde la perspectiva de mantenibilidad.

Estos indicadores deben utilizarse conjuntamente, ya que cada uno evalúa características diferentes del software.

## 9. Comparación antes y después de la refactorización

| Elemento | Métrica | Antes | Después | Interpretación |
|---|---|---:|---:|---|
| Backend Java | Infracciones V(G) | 3 | 0 | Mejora comprobada |
| SessionService | WMC | 38 | 44 | Aumento de complejidad agregada |
| AttendanceService | WMC | 18 | 24 | Aumento de complejidad agregada |
| SessionService | LCOM* | 0,694 | 0,796 | Mayor falta de cohesión según CK |
| AttendanceService | LCOM* | 0,520 | 0,764 | Mayor falta de cohesión según CK |

### 9.1. Análisis

La refactorización permitió disminuir la complejidad ciclomática de los métodos que superaban RNF-01, mediante la aplicación de la técnica *Extract Method*.

Sin embargo, las métricas CK evidencian que el aumento del número de métodos auxiliares puede repercutir en indicadores agregados como WMC y LCOM*.

Por lo tanto, una mejora en la complejidad de métodos individuales no garantiza una mejora simultánea en todos los indicadores de diseño.

### 9.2. Mejoras propuestas

**Prioridad alta:** revisar la distribución de responsabilidades en SessionService y AttendanceService mediante técnicas como *Extract Class*.

**Prioridad media:** reducir las dependencias de los controladores que presentan valores elevados de CBO.

**Prioridad media:** mejorar la documentación interna de las reglas de negocio complejas.

**Prioridad media:** ampliar los escenarios de pruebas de rendimiento para incluir operaciones de escritura.

Las propuestas deberán evaluarse mediante nuevas mediciones y pruebas de regresión antes de considerar sus beneficios como comprobados.

## 10. Responsabilidades

**José Miguel Aya Peralta:** integración y actualización del sistema Attendance Registry, implementación de ajustes funcionales, refactorización de los métodos con complejidad elevada, ejecución de pruebas funcionales y de regresión, actualización de la documentación técnica y organización de las evidencias.

**Luis Fernando Pérez Chala:** configuración y ejecución de las herramientas PMD, CK y CLOC; recopilación de las mediciones iniciales y posteriores; análisis comparativo de complejidad ciclomática, LOC y métricas CK; identificación de problemas de mantenibilidad; elaboración del plan de mejoras priorizado y preparación del informe académico final.

**Andrés Felipe Calle González:** integrante del equipo académico. No se registran contribuciones técnicas verificables en el repositorio para las actividades evaluadas.

La sustentación y la presentación de resultados estarán a cargo de los integrantes que participen efectivamente en ella. Las responsabilidades descritas corresponden a las actividades realizadas y a las evidencias disponibles.

## 11. Estado del documento

**Versión:** 2.0 — Mediciones realizadas y resultados documentados.

**Fecha de actualización:** 9 de noviembre de 2026.

**Responsable del análisis de métricas:** Luis Fernando Pérez Chala.

**Estado:** Se completaron las mediciones de complejidad ciclomática, líneas de código (LOC) y métricas CK. Se documentaron los resultados comparativos y se identificaron oportunidades de mejora en la mantenibilidad del backend.

**Evidencias:** `docs/metricas/pmd-final/`, `docs/metricas/pmd-post-refactor/`, `docs/metricas/ck-final/`, `docs/metricas/ck-post-refactor/` y `docs/metricas/loc-final/`.

## 12. Referencias

- Llanos Mosquera, J. M. *Proyecto Final: Métricas de Mantenibilidad y Evolución aplicadas a una Aplicación Real*. Guía de la asignatura Métricas y Estándares de Calidad.
- McCabe, T. J. (1976). *A Complexity Measure*. IEEE Transactions on Software Engineering.
- Chidamber, S. R., & Kemerer, C. F. (1994). *A Metrics Suite for Object Oriented Design*. IEEE Transactions on Software Engineering.

Las referencias a herramientas y versiones específicas se incorporarán una vez seleccionadas y verificadas.