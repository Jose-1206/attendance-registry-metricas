# Análisis inicial de métricas CK — Attendance Registry

## Alcance

- Componente: backend Java tt-book-api.
- Herramienta: CK 0.7.1-SNAPSHOT.
- Versión de referencia: commit inicial 9f364e.
- Fuente de datos: docs/metricas/ck-inicial/class.csv.
- Código de producción: src/main/java.
- Elementos analizados: 25 de producción y 9 de pruebas.

## Resultados CK — Código de producción

| Métrica | Promedio | Máximo |
|---|---:|---:|
| WMC | 3.72 | 14 |
| DIT | 1.04 | 2 |
| NOC | 0 | 0 |
| CBO | 5.00 | 19 |
| RFC | 5.24 | 26 |
| LCOM | 1.24 | 13 |

## Componentes que requieren revisión

- IdentityService: WMC = 14 y RFC = 26.
- CourseController: CBO = 19.
- AppUser: LCOM = 13.
- Course: LCOM = 9.

## Interpretación

Los resultados muestran una profundidad de herencia baja y
permiten identificar componentes con valores relativamente altos
de complejidad, acoplamiento y falta de cohesión.

Los valores no representan defectos confirmados. Se recomienda
revisar las responsabilidades y dependencias de las clases
destacadas antes de proponer refactorizaciones.

## Limitaciones

- La medición corresponde a la versión inicial disponible.
- Los resultados deben recalcularse después de integrar los
  cambios del backend.
- Los promedios excluyen los elementos ubicados en src/test/java.
- Las métricas deben interpretarse según el tipo de clase.
