#!/usr/bin/env bash
# Helper script (bash) — build, install and run the app; exits on first failure.
# Usage: ./run-local.sh

set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
cd "$HERE"

echo ">>> Building APK (assembleDebug)"
./gradlew clean :app:assembleDebug --no-daemon --stacktrace

echo ">>> Installing APK on connected device/emulator"
./gradlew :app:installDebug --no-daemon --stacktrace

echo ">>> Devices attached:"
adb devices

echo ">>> Starting MainActivity"
adb shell am start -n com.team3.vinyls/.MainActivity

exit 0

