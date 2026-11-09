# Informe comparativo de métricas — Attendance Registry

## 1. Objetivo

Evaluar la mantenibilidad del backend Java mediante complejidad
ciclomática, líneas de código (LOC) y métricas CK, comparando los
resultados antes y después de la refactorización.

## 2. Herramientas

- Java 17 y Maven.
- PMD 7.17.0.
- CK de Mauricio Aniche.
- PowerShell para procesar las mediciones.

## 3. Complejidad ciclomática y RNF-01

El requisito RNF-01 establece que los métodos del backend deben
tener una complejidad ciclomática V(G) menor o igual a 10.

Antes de la refactorización, PMD detectó:

| Clase | Método | V(G) anterior |
|---|---|---:|
| SessionService | validateRequest | 17 |
| AttendanceService | registerAttendance | 15 |
| SessionService | requireCourseAccess | 11 |

Después de integrar el PR #6 se repitió el análisis:

| Indicador | Antes | Después |
|---|---:|---:|
| Violaciones PMD | 3 | 0 |

El RNF-01 cumple según la configuración de PMD utilizada.

## 4. Líneas de código (LOC)

| Indicador | Versión inicial | Versión ampliada |
|---|---:|---:|
| Archivos Java de producción | 20 | 43 |
| Líneas físicas | 1028 | 2473 |
| Líneas no vacías | 836 | 1994 |
| Líneas en blanco | 192 | 479 |

El aumento se relaciona con la ampliación funcional del backend.
No constituye por sí mismo una mejora o deterioro de calidad.

## 5. Métricas CK antes y después de la refactorización

Se analizaron 48 clases e interfaces de producción en cada medición,
excluyendo los archivos de pruebas.

| Métrica | Antes | Después |
|---|---:|---:|
| WMC | 4.79 | 5.04 |
| DIT | 1.02 | 1.02 |
| NOC | 0.00 | 0.00 |
| CBO | 5.60 | 5.60 |
| RFC | 5.62 | 5.88 |
| LCOM | 2.56 | 4.94 |

La complejidad ciclomática mejoró según PMD, aunque WMC, RFC y
LCOM aumentaron. Esto muestra que distintas métricas evalúan
aspectos diferentes del diseño.

Conviene revisar especialmente las clases con mayor LCOM para
determinar si necesitan una mejor distribución de responsabilidades.

## 6. Pruebas y verificación

- Compilación Java: BUILD SUCCESS.
- PMD después de la refactorización: cero violaciones.
- Pruebas automatizadas ejecutadas: 134.
- Pruebas exitosas: 134.
- Fallos: 0.
- Errores: 0.
- Pruebas omitidas: 0.
- Resultado de Maven: BUILD SUCCESS.

La prueba AttBookApiApplicationTests.contextLoads se ejecutó
correctamente después de configurar el entorno local con
PostgreSQL 17, DB_URL, DB_USERNAME, DB_PASSWORD y una clave
JWT_SECRET válida codificada en Base64.

La validación se realizó mediante el comando mvn test,
sin modificar el código del backend ni utilizar una clave
JWT de producción.
## 7. Evidencias

- docs/metricas/ck-inicial/
- docs/metricas/ck-final/
- docs/metricas/ck-post-refactor/
- docs/metricas/pmd-final/pmd.xml
- docs/metricas/pmd-post-refactor/pmd.xml

## 8. Conclusiones

1. Las violaciones de complejidad detectadas por PMD bajaron
   de tres a cero después de la refactorización.
2. El RNF-01 cumple según el análisis estático configurado.
3. CK presenta resultados mixtos: algunas métricas permanecieron
   estables y otras aumentaron.
4. La compilación y las 134 pruebas automatizadas fueron exitosas. La suite se ejecutó
   con PostgreSQL 17 y una clave JWT de pruebas configurados correctamente.
5. Los reportes conservados permiten reproducir y comparar
   las mediciones.



