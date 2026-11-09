
# RNF-02 - Pruebas de seguridad de Attendance Registry

$ErrorActionPreference = "Stop"
$base = "http://localhost:8086"
$resultados = @()

function Probar {
    param(
        [string]$Nombre,
        [string]$Metodo,
        [string]$Ruta,
        [int]$Esperado,
        [AllowNull()][string]$Token
    )

    $headers = @{ Accept = "application/json" }

    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $parametros = @{
        Uri = "$base$Ruta"
        Method = $Metodo
        Headers = $headers
        UseBasicParsing = $true
        TimeoutSec = 15
        ErrorAction = "Stop"
    }

    if ($Metodo -in @("POST", "PUT", "PATCH")) {
        $parametros.ContentType = "application/json"
        $parametros.Body = if ($Ruta -eq "/api/v1/asistencias") {
    '{"qr":"codigo-de-prueba-sin-validez"}'
}
else {
    "{}"
}
    }

    $codigo = 0

    try {
        $respuesta = Invoke-WebRequest @parametros
        $codigo = [int]$respuesta.StatusCode
    }
    catch {
        if ($_.Exception.Response) {
            $codigo = [int]$_.Exception.Response.StatusCode
        }
    }

    $estado = if ($codigo -eq $Esperado) {
        "PASS"
    }
    else {
        "FAIL"
    }

    $script:resultados += [PSCustomObject]@{
        Prueba = $Nombre
        Metodo = $Metodo
        Ruta = $Ruta
        Esperado = $Esperado
        Obtenido = $codigo
        Resultado = $estado
    }

    Write-Host "[$estado] $Nombre - HTTP $codigo"
}

# Verificar que el portal responda
$salud = Invoke-WebRequest `
    -Uri "$base/health" `
    -UseBasicParsing `
    -TimeoutSec 10

if ($salud.StatusCode -ne 200) {
    throw "El portal no está disponible."
}

# Operaciones protegidas
$casos = @(
    @{ M = "GET";    R = "/api/v1/usuarios/me" },
    @{ M = "GET";    R = "/api/v1/cursos" },
    @{ M = "GET";    R = "/api/v1/cursos/1" },
    @{ M = "POST";   R = "/api/v1/cursos" },
    @{ M = "PUT";    R = "/api/v1/cursos/1" },
    @{ M = "DELETE"; R = "/api/v1/cursos/1" },
    @{ M = "GET";    R = "/api/v1/enrollments/me" },
    @{ M = "POST";   R = "/api/v1/enrollments/courses/1" },
    @{ M = "GET";    R = "/api/v1/sessions/1" },
    @{ M = "GET";    R = "/api/v1/sessions/courses/1" },
    @{ M = "POST";   R = "/api/v1/sessions" },
    @{ M = "PUT";    R = "/api/v1/sessions/1" },
    @{ M = "DELETE"; R = "/api/v1/sessions/1" },
    @{ M = "POST";   R = "/api/v1/clases/1/qr" },
    @{ M = "GET";    R = "/api/v1/clases/1/asistencias" },
    @{ M = "GET";    R = "/api/v1/asistencias/historial" },
    @{ M = "POST";   R = "/api/v1/asistencias" }
)

Write-Host "`n=== ACCESO SIN AUTENTICACION ==="

foreach ($caso in $casos) {
    Probar `
        -Nombre "Sin autenticacion" `
        -Metodo $caso.M `
        -Ruta $caso.R `
        -Esperado 401 `
        -Token $null
}

Write-Host "`n=== TOKEN INVALIDO ==="

foreach ($caso in $casos) {
    Probar `
        -Nombre "Token invalido" `
        -Metodo $caso.M `
        -Ruta $caso.R `
        -Esperado 401 `
        -Token "token-invalido-de-prueba"
}

Write-Host "`n=== AUTENTICACION DE PRUEBA ==="

# La contraseña no se guarda en el archivo
$clave = Read-Host `
    "Contraseña de las cuentas de prueba" `
    -AsSecureString

$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($clave)

try {
    $password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)

    function Login {
        param([string]$Email, [string]$Password)

        $body = @{
            email = $Email
            password = $Password
        } | ConvertTo-Json -Compress

        $respuesta = Invoke-RestMethod `
            -Uri "$base/api/v1/auth/login" `
            -Method POST `
            -ContentType "application/json" `
            -Body $body

        if (-not $respuesta.token) {
            throw "No se recibió un token de autenticación."
        }

        return $respuesta.token
    }

    $docente = Login `
        "teacher.owner@example.test" $password

    $estudiante = Login `
        "student.enrolled@example.test" $password
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    Remove-Variable password -ErrorAction SilentlyContinue
}

Write-Host "`n=== CONTROL DE ROLES ==="

Probar "Estudiante consulta cursos del docente" `
    GET "/api/v1/cursos" 403 $estudiante

Probar "Estudiante intenta crear un curso" `
    POST "/api/v1/cursos" 403 $estudiante

Probar "Estudiante intenta crear una sesion" `
    POST "/api/v1/sessions" 403 $estudiante

Probar "Docente consulta inscripciones personales" `
    GET "/api/v1/enrollments/me" 403 $docente

Probar "Docente consulta historial de estudiante" `
    GET "/api/v1/asistencias/historial" 403 $docente

Probar "Docente intenta registrar asistencia" `
    POST "/api/v1/asistencias" 403 $docente

# Liberar referencias a los tokens
Remove-Variable docente, estudiante -ErrorAction SilentlyContinue

# Guardar resultados
$carpeta = $PSScriptRoot

$resultados | Export-Csv `
    -Path (Join-Path $carpeta "resultados-rnf-02.csv") `
    -NoTypeInformation `
    -Encoding UTF8

$total = $resultados.Count
$aprobadas = @($resultados | Where-Object {
    $_.Resultado -eq "PASS"
}).Count

$fallidas = $total - $aprobadas

Write-Host "`n=== RESUMEN RNF-02 ==="
Write-Host "Total: $total"
Write-Host "Aprobadas: $aprobadas"
Write-Host "Fallidas: $fallidas"

$informe = @"
# Informe de seguridad RNF-02

## Información general

- Proyecto: Attendance Registry.
- Fecha de ejecución: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss').
- Tipo de prueba: Seguridad de endpoints HTTP.
- Entorno: Docker Compose local.

## Objetivo

Verificar que las operaciones protegidas de la API
rechacen solicitudes sin autenticación, con tokens
inválidos o con roles que no tienen autorización.

## Resultados

- Casos ejecutados: $total.
- Casos aprobados: $aprobadas.
- Casos fallidos: $fallidas.

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
"@

$informe | Set-Content `
    -Path (Join-Path $carpeta "informe-rnf-02.md") `
    -Encoding UTF8

if ($fallidas -gt 0) {
    Write-Host "`nRNF-02: Se detectaron fallos."
    exit 1
}

Write-Host "`nRNF-02: Todas las comprobaciones aprobaron."
