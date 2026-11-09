# Análisis inicial de complejidad ciclomática — Attendance Registry

## Alcance

- Componente: backend Java `att-book-api`.
- Versión de referencia: commit inicial `b9f364e`.
- Herramientas: CK 0.7.1-SNAPSHOT y PMD 7.17.0.
- Fuente de datos: `docs/metricas/ck-inicial/method.csv`.
- Código de producción: `src/main/java`.
- Métodos y constructores analizados: 63.

## Criterio de evaluación

Se establece como criterio de referencia una complejidad
ciclomática V(G) menor o igual a 10 por método.

Para PMD se configuró la regla CyclomaticComplexity con
methodReportLevel = 11.

## Resultados

- Elementos de método analizados: 63.
- Complejidad máxima registrada por CK: 8.
- Elementos con complejidad superior a 10: 0.
- Infracciones reportadas por PMD: 0.

## Métodos con mayor complejidad

| Clase | Método | Línea | V(G) CK | LOC CK |
|---|---|---:|---:|---:|
| SecurityConfig | convertAuthentication | 73 | 8 | 15 |
| JwtService | issueToken | 45 | 7 | 15 |
| IdentityService | register | 42 | 5 | 17 |
| IdentityService | login | 79 | 4 | 11 |
| CourseService | requireTeacher | 89 | 4 | 9 |
| JwtService | Constructor | 32 | 3 | 9 |
| JwtConfiguration | Constructor | 30 | 3 | 14 |
| CourseController | actorFrom | 85 | 2 | 6 |
| CurrentUserController | currentUser | 25 | 2 | 6 |
| IdentityService | requireSupportedPasswordLength | 125 | 2 | 5 |

## Interpretación

Los 63 elementos de método y constructor analizados mediante
CK presentan valores de complejidad que no superan el límite
de referencia de 10.

El valor máximo corresponde al método
SecurityConfig.convertAuthentication, con V(G) = 8,
seguido de JwtService.issueToken, con V(G) = 7.

Estos métodos son candidatos para una revisión de legibilidad
y mantenibilidad, pero los resultados no justifican por sí
solos una refactorización.

## Limitaciones

- Los resultados corresponden a la versión inicial del backend.
- CK y PMD pueden emplear criterios diferentes de complejidad.
- El reporte de PMD identifica infracciones del umbral, no
  proporciona valores individuales para todos los métodos.
- Los resultados deben actualizarse después de integrar los
  cambios de la versión final del proyecto.
