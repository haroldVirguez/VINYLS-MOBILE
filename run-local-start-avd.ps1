<#
PowerShell helper: Inicia un AVD (o usa el first AVD disponible), espera a que aparezca en adb,
luego construye, instala y arranca la MainActivity.
Usage:
  .\run-local-start-avd.ps1           # usa el primer AVD listado
  .\run-local-start-avd.ps1 -AvdName "Pixel_6_API_36"
#>
param(
    [string]$AvdName = ""
)

Set-StrictMode -Version Latest
$here = Split-Path -Path $MyInvocation.MyCommand.Path -Parent
Set-Location $here

# Resolve Android SDK emulator path
$androidSdk = $env:ANDROID_SDK_ROOT
if (-not $androidSdk) { $androidSdk = $env:ANDROID_HOME }
if (-not $androidSdk) {
    # Common default path
    $possible = "$env:LOCALAPPDATA\Android\Sdk"
    if (Test-Path $possible) { $androidSdk = $possible }
}
if (-not $androidSdk) { Write-Error "ANDROID_SDK_ROOT / ANDROID_HOME not set and default SDK not found. Set it and retry."; exit 1 }

$emulatorExe = Join-Path $androidSdk "emulator\emulator.exe"
if (-not (Test-Path $emulatorExe)) { Write-Error "emulator.exe not found at $emulatorExe"; exit 1 }

# List AVDs
$avds = & $emulatorExe -list-avds 2>$null | Where-Object { $_ -ne "" }
if (-not $avds -or $avds.Count -eq 0) { Write-Error "No AVDs found. Create one with AVD Manager."; exit 1 }

if ([string]::IsNullOrEmpty($AvdName)) { $AvdName = $avds[0] }
Write-Host "Starting AVD: $AvdName"
# Start emulator (non-blocking)
Start-Process -FilePath $emulatorExe -ArgumentList "-avd", $AvdName

# Wait for device
Write-Host 'Waiting for device (adb)...'
& adb kill-server 2>$null
& adb start-server 2>$null
& adb wait-for-device

# Check devices
$devices = & adb devices
Write-Host $devices

# Build, install, run
Write-Host 'Building APK (assembleDebug)'
& '.\gradlew.bat' clean ':app:assembleDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) { Write-Error 'Build failed'; exit $LASTEXITCODE }

Write-Host 'Installing APK (installDebug)'
& '.\gradlew.bat' ':app:installDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) { Write-Error 'Install failed'; exit $LASTEXITCODE }

Write-Host 'Starting MainActivity'
& adb shell am start -n com.team3.vinyls/.MainActivity

exit 0

