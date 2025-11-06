<#
run-e2e-kraken.ps1
Script breve para Windows (PowerShell) que automatiza los pasos mínimos para ejecutar las E2E con kraken.
Uso:
  - Abrir PowerShell en la raíz del repo.
  - ./run-e2e-kraken.ps1           # instala app, levanta mock+appium y ejecuta tests
  - ./run-e2e-kraken.ps1 -InstallApp:$false   # no reinstala la app
  - ./run-e2e-kraken.ps1 -Flavor prod         # usa flavor prod (no mock)

Notas:
 - El script arranca el mock API y Appium en procesos separados (background). Deberás cerrar esos procesos manualmente al terminar (o reiniciar la terminal).
 - Requiere PowerShell 5+ y que `npm` y `.\gradlew.bat` estén en PATH o accesibles desde la carpeta del repo.
#>

param(
    [switch]$InstallApp = $true,
    [string]$Flavor = "e2e"
)

Write-Host "[e2e] Flavor: $Flavor  | InstallApp: $InstallApp"

# 1) Instalar el flavor (si se pidió)
if ($InstallApp) {
    Write-Host '[e2e] Instalando APK en el emulador...'
    & .\gradlew.bat ":app:install${Flavor}Debug"
    if ($LASTEXITCODE -ne 0) { Write-Error 'gradlew install falló.'; exit $LASTEXITCODE }
}

# 2) Levantar mock API (background)
Write-Host '[e2e] Levantando mock API (background)...'
Start-Process -FilePath "cmd.exe" -ArgumentList "/c npm --prefix .\\e2e-mock-api start" -WindowStyle Minimized

# 3) Ejecutar Appium (background)
Write-Host '[e2e] Iniciando Appium (background)...'
Start-Process -FilePath "cmd.exe" -ArgumentList "/c npm --prefix .\\e2e-kraken run appium" -WindowStyle Minimized

# 4) Ejecutar pruebas
Write-Host '[e2e] Ejecutando pruebas (espera a Appium)...'
Start-Sleep -Seconds 6  # pequeño retardo para que Appium arranque (ajusta si necesitas más)

Set-Location .\e2e-kraken
npm run test:android

$code = $LASTEXITCODE
if ($code -eq 0) { Write-Host '[e2e] Tests completados OK.' } else { Write-Error "[e2e] Tests fallaron con código $code" }

Write-Host "[e2e] Nota: detén los procesos de mock API y Appium manualmente cuando termines (Task Manager o Stop-Process).
"
