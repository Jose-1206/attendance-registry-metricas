# Informe de seguridad RNF-02

## Información general

- Proyecto: Attendance Registry.
- Fecha de ejecución: 2026-11-09 13:30:24.
- Tipo de prueba: Seguridad de endpoints HTTP.
- Entorno: Docker Compose local.

## Objetivo

Verificar que las operaciones protegidas de la API
rechacen solicitudes sin autenticación, con tokens
inválidos o con roles que no tienen autorización.

## Resultados

- Casos ejecutados: 40.
- Casos aprobados: 40.
- Casos fallidos: 0.

## Evidencias

Los resultados individuales se encuentran en
resultados-rnf-02.csv.

## Alcance y limitaciones

La prueba verifica códigos de respuesta HTTP
para los escenarios definidos.

No representa una auditoría de seguridad completa.
No evalúa ataques de inyección, vulnerabilidades
de dependencias ni todas las combinaciones de
permisos sobre recursos individuales.
