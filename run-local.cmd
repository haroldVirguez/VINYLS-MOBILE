@echo off
rem Helper script (Windows cmd) — build, install and run the app; exits on first failure.
setlocal enabledelayedexpansion
cd /d %~dp0

echo >>> Building APK (assembleDebug)
call gradlew.bat clean :app:assembleDebug --no-daemon --stacktrace
 if %ERRORLEVEL% NEQ 0 (
  echo Build failed. Aborting.
  exit /b %ERRORLEVEL%
)

echo >>> Installing APK on connected device/emulator
call gradlew.bat :app:installDebug --no-daemon --stacktrace
 if %ERRORLEVEL% NEQ 0 (
  echo Install failed. Aborting.
  exit /b %ERRORLEVEL%
)

echo >>> Devices attached:
adb devices

echo >>> Starting MainActivity
adb shell am start -n com.team3.vinyls/.MainActivity

endlocal
exit /b 0

