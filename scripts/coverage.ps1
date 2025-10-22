# PowerShell script to run JaCoCo coverage for module :app
# Run this from the repository or call the script directly from PowerShell.
Set-StrictMode -Version Latest
$root = Split-Path -Parent $PSScriptRoot
Set-Location $PSScriptRoot
Write-Host "Running JaCoCo coverage for module :app..."
& '.\gradlew.bat' ':app:jacocoTestReport' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) {
    Write-Error "Gradle task failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
$report = Join-Path $PSScriptRoot "app\build\reports\jacoco\jacocoTestReport\html\index.html"
if (Test-Path $report) {
    Start-Process $report
} else {
    Write-Error "Report not found: $report"
    exit 1
}
@echo off
REM Script to run unit tests and generate JaCoCo coverage report (Windows cmd)
REM Run this from anywhere; the script will change to the repo root.
cd /d "%~dp0\.."
echo Running JaCoCo coverage for module :app...
call gradlew.bat :app:jacocoTestReport --no-daemon --stacktrace
if %ERRORLEVEL% neq 0 (
  echo Gradle task failed with errorlevel %ERRORLEVEL%
  exit /b %ERRORLEVEL%
)
set REPORT_PATH=app\build\reports\jacoco\jacocoTestReport\html\index.html
if exist "%REPORT_PATH%" (
  start "" "%REPORT_PATH%"
) else (
  echo Report not found: %REPORT_PATH%
  exit /b 1
)

