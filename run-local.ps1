# PowerShell helper script — build, install and run the app; exits on first failure.
# Usage: Open PowerShell (preferiblemente 7+, otherwise the script checks exit codes) and run: .\run-local.ps1

Set-StrictMode -Version Latest
$here = Split-Path -Path $MyInvocation.MyCommand.Path -Parent
Set-Location $here

Write-Host '>>> Building APK (assembleDebug)'
& '.\gradlew.bat' clean ':app:assembleDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) {
    Write-Error 'Build failed. Aborting.'
    exit $LASTEXITCODE
}

Write-Host '>>> Installing APK on connected device/emulator'
& '.\gradlew.bat' ':app:installDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) {
    Write-Error 'Install failed. Aborting.'
    exit $LASTEXITCODE
}

Write-Host '>>> Devices attached:'
adb devices

Write-Host '>>> Starting MainActivity'
adb shell am start -n com.team3.vinyls/.MainActivity

exit 0

