# Informe comparativo de métricas de calidad — Attendance Registry

**Asignatura:** Métricas y Estándares de Calidad
**Responsable de las mediciones:** Luis Fernando Pérez Chala
**Fecha:** 9 de noviembre de 2026

## 1. Objetivo

Evaluar la mantenibilidad del backend Java de Attendance Registry mediante tres grupos de métricas: complejidad ciclomática V(G), líneas de código (LOC) y métricas CK.

Se comparan las mediciones disponibles de la versión inicial, la versión funcional ampliada y la versión posterior a la refactorización, con el propósito de identificar mejoras y problemas de diseño pendientes.

## 2. Herramientas utilizadas

| Herramienta | Versión | Propósito |
|---|---|---|
| Java | 17 | Entorno de desarrollo |
| Maven | Según configuración del proyecto | Compilación y pruebas |
| PMD | 7.17.0 | Complejidad ciclomática |
| CK de Mauricio Aniche | Versión pendiente de registrar | Métricas orientadas a objetos |
| CLOC | 2.08 | Conteo de código, comentarios y líneas en blanco |
| PowerShell | Entorno Windows | Ejecución de herramientas y procesamiento |

Las métricas principales se aplicaron sobre el backend Java de producción, excluyendo las clases de prueba cuando corresponde.

## 3. Complejidad ciclomática — V(G)

El requisito no funcional RNF-01 establece que los métodos del backend deben tener una complejidad ciclomática menor o igual a 10.

### 3.1. Resultados antes de la refactorización

| Clase | Método | V(G) anterior | Umbral |
|---|---|---:|---:|
| SessionService | validateRequest | 17 | ≤ 10 |
| AttendanceService | registerAttendance | 15 | ≤ 10 |
| SessionService | requireCourseAccess | 11 | ≤ 10 |

PMD identificó tres métodos que superaban el umbral establecido.

### 3.2. Resultados posteriores

Después de integrar el PR #6, se repitió el análisis estático con PMD.

| Indicador | Antes | Después |
|---|---:|---:|
| Infracciones de complejidad | 3 | 0 |

La refactorización aplicó principalmente la técnica *Extract Method* para separar validaciones y responsabilidades.

El resultado posterior registró cero infracciones del umbral configurado. Por lo tanto, RNF-01 cumple según el análisis realizado mediante PMD.

Las evidencias se encuentran en `docs/metricas/pmd-final/` y `docs/metricas/pmd-post-refactor/`.

## 4. Líneas de código — LOC

La medición actual se realizó con **CLOC 2.08** sobre el directorio:

`sistema/att-book-api/src/main/java`

Se analizaron exclusivamente los archivos Java de producción.

### 4.1. Resultados actuales

| Indicador | Resultado |
|---|---:|
| Archivos Java de producción | 43 |
| Líneas de código según CLOC | 2.017 |
| Líneas de comentarios | 31 |
| Líneas en blanco | 489 |
| Líneas físicas totales | 2.537 |

Las líneas físicas totales se obtienen sumando código, comentarios y líneas en blanco:

**2.017 + 31 + 489 = 2.537**

### 4.2. Porcentaje de comentarios

Se aplicó la fórmula:

**Porcentaje de comentarios = (líneas de comentarios / líneas físicas totales) × 100**

Sustituyendo los valores:

**(31 / 2.537) × 100 = 1,22 %**

El porcentaje de comentarios del backend es aproximadamente del 1,22 %.

Este resultado indica que una pequeña proporción de las líneas físicas está dedicada exclusivamente a comentarios. No implica automáticamente una documentación insuficiente, porque también deben considerarse la claridad del código, sus nombres y la documentación externa.

### 4.3. Comparación entre versiones

| Indicador | Versión inicial | Versión ampliada | Versión actual |
|---|---:|---:|---:|
| Archivos Java | 20 | 43 | 43 |
| Líneas físicas | 1.028 | 2.473 | 2.537 |
| Líneas en blanco | 192 | 479 | 489 |
| Líneas no vacías | 836 | 1.994 | No comparable directamente |
| Líneas de código según CLOC | No medidas | No medidas | 2.017 |
| Líneas de comentarios según CLOC | No medidas | No medidas | 31 |

El aumento de tamaño se relaciona con la ampliación funcional del backend y los cambios posteriores de mantenimiento.

Un incremento en LOC no significa necesariamente una mejora ni un deterioro de calidad. Su interpretación debe relacionarse con las funcionalidades implementadas y las demás métricas.

Los resultados históricos no deben confundirse con las categorías de CLOC, porque las líneas no vacías no equivalen necesariamente a líneas de código.

### 4.4. Análisis LOC por método

Para complementar el conteo global realizado con CLOC 2.08, se analizaron las métricas de longitud de métodos del archivo `docs/metricas/ck-post-refactor/method.csv`.

El conjunto contiene **336 registros de métodos y constructores**. Se filtraron los registros identificados como constructores y se aplicó el umbral de alerta establecido en la guía académica: más de 50 líneas de código por método.

**Resultados:**

| Indicador | Resultado |
|---|---:|
| Registros de métodos y constructores | 336 |
| Métodos no constructores con LOC mayor que 50 | 0 |
| Umbral de referencia | 50 LOC |

De acuerdo con los resultados de CK, ningún método no constructor supera el umbral establecido.

Esta verificación complementa la medición global de CLOC, aunque las dos herramientas pueden emplear criterios diferentes para contabilizar líneas de código.

**Evidencia:** `docs/metricas/loc-final/metodos-mayores-50.csv` y `docs/metricas/ck-post-refactor/method.csv`.

## 5. Métricas CK antes y después de la refactorización

Se analizaron 48 clases e interfaces de producción en cada medición posterior a la integración, excluyendo los archivos de pruebas.

### 5.1. Resultados promedio

| Métrica | Antes | Después |
|---|---:|---:|
| WMC | 4,79 | 5,04 |
| DIT | 1,02 | 1,02 |
| NOC | 0,00 | 0,00 |
| CBO | 5,60 | 5,60 |
| RFC | 5,62 | 5,88 |
| LCOM | 2,56 | 4,94 |

Los resultados muestran que WMC, RFC y LCOM aumentaron después de la refactorización, mientras que DIT, NOC y CBO permanecieron estables.

Esto demuestra que reducir la complejidad ciclomática de métodos individuales no garantiza una mejora simultánea en todas las métricas de diseño.

### 5.2. Clases que requieren atención

| Clase | Métrica | Antes | Después |
|---|---|---:|---:|
| SessionService | WMC | 38 | 44 |
| AttendanceService | WMC | 18 | 24 |
| SessionService | LCOM* | 0,694 | 0,796 |
| AttendanceService | LCOM* | 0,520 | 0,764 |

La clase `SessionService` presenta un WMC elevado, lo que indica que concentra una complejidad agregada considerable.

Por su parte, `AttendanceService` también presenta un incremento de WMC.

El aumento de LCOM* en estas clases sugiere oportunidades para revisar la cohesión y la distribución de responsabilidades.

Es importante distinguir entre LCOM y LCOM*: el umbral normalizado de 0,7 corresponde a LCOM*, no a la variante LCOM presentada en la tabla general.

### 5.3. Interpretación de CK

Los resultados deben interpretarse como indicadores de posibles dificultades de mantenimiento, no como evidencia automática de defectos funcionales.

La refactorización mejoró la complejidad ciclomática según PMD, pero dejó oportunidades de mejora en la organización de las clases.

Por ello, se recomienda combinar los resultados de CK, LOC y PMD al evaluar la mantenibilidad.

## 6. Pruebas y verificación

Según la última ejecución documentada del backend:

| Indicador | Resultado |
|---|---:|
| Compilación Java | BUILD SUCCESS |
| Pruebas automatizadas | 134 |
| Pruebas exitosas | 134 |
| Fallos | 0 |
| Errores | 0 |
| Pruebas omitidas | 0 |
| Resultado Maven | BUILD SUCCESS |
| Infracciones PMD posteriores | 0 |

La prueba `AttBookApiApplicationTests.contextLoads` se ejecutó correctamente después de configurar el entorno de pruebas con PostgreSQL 17 y las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y una clave `JWT_SECRET` válida codificada en Base64.

La validación se realizó mediante `mvn test`, sin modificar el código del backend ni utilizar una clave JWT de producción.

Estas pruebas respaldan las verificaciones realizadas, pero no garantizan la ausencia de todos los defectos posibles.

## 7. Plan de mejoras priorizado

| Prioridad | Problema identificado | Mejora propuesta | Resultado esperado |
|---|---|---|---|
| Alta | WMC elevado en SessionService | Aplicar Extract Class y separar responsabilidades | Reducir complejidad agregada por clase |
| Alta | Aumento de WMC y LCOM* en AttendanceService | Separar validaciones, reglas de QR y persistencia | Mejorar cohesión y organización |
| Media | CBO elevado en algunos controladores | Revisar dependencias y simplificar controladores | Reducir acoplamiento |
| Media | Pocas líneas de comentarios internos | Documentar reglas de negocio complejas | Facilitar comprensión y mantenimiento |
| Media | Pruebas de rendimiento centradas en consultas | Incorporar escenarios de escritura | Ampliar evidencia de rendimiento |

Estas propuestas constituyen un plan de evolución futura. No representan defectos funcionales confirmados.

## 8. Evidencias

Se conservan los siguientes archivos para respaldar los resultados:

- `docs/metricas/ck-inicial/class.csv`
- `docs/metricas/ck-inicial/method.csv`
- `docs/metricas/ck-final/class.csv`
- `docs/metricas/ck-final/method.csv`
- `docs/metricas/ck-post-refactor/class.csv`
- `docs/metricas/ck-post-refactor/method.csv`
- `docs/metricas/pmd-final/pmd.xml`
- `docs/metricas/pmd-post-refactor/pmd.xml`
- `docs/metricas/loc-final/resumen.csv`

Los archivos CSV y XML constituyen la evidencia de las mediciones realizadas.

## 9. Conclusiones

1. La refactorización permitió reducir de tres a cero las infracciones de complejidad ciclomática identificadas mediante PMD.

2. El requisito RNF-01 cumple según el análisis estático realizado con la configuración de PMD utilizada.

3. Las métricas CK muestran resultados mixtos, ya que algunas permanecieron estables y otras aumentaron después de la refactorización.

4. La medición con CLOC 2.08 identificó 2.537 líneas físicas en el backend Java, distribuidas en 2.017 líneas de código, 31 líneas de comentarios y 489 líneas en blanco.

5. La ejecución documentada del backend registra 134 pruebas automatizadas exitosas, sin fallos ni errores.

6. Las clases SessionService y AttendanceService presentan oportunidades de mejora en cohesión y distribución de responsabilidades.

7. La calidad interna del software debe evaluarse utilizando diferentes métricas de manera complementaria, evitando depender de un único indicador.

8. LOC por método ya se realizó usando los resultados de CK y no se encontraron métodos no constructores superiores a 50 LOC según ese criterio.

## 10. Referencias

- McCabe, T. J. (1976). *A Complexity Measure*. IEEE Transactions on Software Engineering.
- Chidamber, S. R., y Kemerer, C. F. (1994). *A Metrics Suite for Object Oriented Design*. IEEE Transactions on Software Engineering.
- PMD. *PMD Source Code Analyzer*. Versión 7.17.0.
- AlDanial. *CLOC — Count Lines of Code*. Versión 2.08.
- Aniche, M. *CK — Java Code Metrics Calculator*.
- Guía del proyecto final de la asignatura Métricas y Estándares de Calidad.
