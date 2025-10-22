# Vinyls Mobile

Aplicación Android (Kotlin) con patrones MVVM, Repository y Service Adapter.

Resumen
- Proyecto de ejemplo que muestra una lista de discos (Albums) y una pantalla de detalle.
- Pensado para ejecutarse desde Android Studio o desde la línea de comandos usando el Gradle wrapper.

Requisitos
- Android Studio Giraffe (o superior) recomendado.
- Gradle JDK: 17 (Settings → Build Tools → Gradle → Gradle JDK).
- Android SDK: Platform y Build-Tools para API 36 (o ajustar `compileSdk`/`targetSdk` a la API que tengas instalada).
- Dispositivo Android o emulador con ADB disponible.

Preparación (una vez)
1. Instala Android Studio y las SDK Platforms/Build Tools necesarias (API 36 si usas `compileSdk = 36`).
2. Abre Android Studio y abre este proyecto (la carpeta raíz contiene `settings.gradle.kts`).
3. En Android Studio: File → Settings → Build, Execution, Deployment → Gradle:
   - Asegúrate de usar "Gradle wrapper".
   - Selecciona Gradle JDK 17.
4. Sync Project with Gradle Files (SafeArgs genera clases durante el sync/build).

Emulador o dispositivo físico
- Emulador: abre AVD Manager y crea un dispositivo con API >= la que use `compileSdk`.
- Dispositivo físico: activa "Developer options" y "USB debugging", conecta por USB y ejecuta `adb devices` para comprobar la conexión.

Comandos útiles (Windows)
- Usar siempre el Gradle wrapper incluido (`gradlew.bat`) desde la raíz del proyecto.

Nota importante: en la documentación se asume que ya has abierto una terminal en la raíz del repositorio (el directorio que contiene `settings.gradle.kts`). No uses rutas absolutas en los ejemplos: ejecuta los comandos directamente desde el root del repo.

En cmd.exe (ejecuta desde la raíz del repo):
```bat
gradlew.bat clean :app:assembleDebug --no-daemon --stacktrace
gradlew.bat :app:installDebug --no-daemon --stacktrace
adb devices
adb shell am start -n com.team3.vinyls/.MainActivity
```

En PowerShell (Windows PowerShell 5.x) — ejecuta desde la raíz del repo; evita `&&` en PowerShell 5.x y usa `&` si necesitas invocar el wrapper:
```powershell
& '.\gradlew.bat' 'clean' ':app:assembleDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) { Write-Error 'build failed'; exit $LASTEXITCODE }
& '.\gradlew.bat' ':app:installDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) { Write-Error 'install failed'; exit $LASTEXITCODE }
adb devices
adb shell am start -n com.team3.vinyls/.MainActivity
```

En PowerShell 7+ puedes usar `&&` como en bash.

Descripción de los pasos CLI
- `clean :app:assembleDebug` — limpia y compila el APK debug.
- `:app:installDebug` — instala el APK en el dispositivo/emulador conectado.
- `adb shell am start -n <package>/<activity>` — arranca la MainActivity de la app.


Problemas comunes y soluciones rápidas
- PowerShell: si obtienes el error "El token '&&' no es un separador de instrucciones válido", usa `&` o ejecuta comandos en varias líneas (véase ejemplos).
- AGP vs compileSdk: si recibes una advertencia sobre `compileSdk = 36` y la versión de AGP, puedes suprimirla añadiendo en `gradle.properties`:
```
android.suppressUnsupportedCompileSdk=36
```
(O actualiza AGP a una versión compatible).
- Gradle wrapper: si necesitas forzar la versión de Gradle localmente:
```bat
gradlew.bat wrapper --gradle-version 8.7 --distribution-type bin
```
- NoSuchMethodError / errores en tests unitarios: muchas pruebas unitarias dependen de fakes/mocks de clases Android. Si ves excepciones en tests, revisa `app/src/test/...` y los mocks; para ejecutar solo la app sin tests, usa `assembleDebug`/`installDebug` (no `check` ni `build` si `check` invoca pruebas).

## Ejecutar pruebas unitarias y generar cobertura (JaCoCo)

Esta sección muestra comandos copy/paste para ejecutar las pruebas unitarias del módulo `app` y generar informes de cobertura usando JaCoCo.

Notas previas:
- Usar siempre el Gradle wrapper incluido (`gradlew.bat`) desde la raíz del proyecto.
- En este proyecto ya hay configurada una tarea `jacocoTestReport` en `app/build.gradle.kts` que produce HTML y XML.
- La verificación de cobertura (`jacocoTestCoverageVerification`) está enlazada al `check` y puede fallar si no alcanzas el umbral configurado (p. ej. 80%).

Comandos (Windows - cmd.exe)

1) Ejecutar sólo los tests unitarios (JUnit4) — desde la raíz del repo:
```bat
gradlew.bat :app:testDebugUnitTest --no-daemon --stacktrace
```

2) Ejecutar tests y generar reporte JaCoCo (HTML + XML):
```bat
gradlew.bat :app:jacocoTestReport --no-daemon --stacktrace
```
- La tarea `:app:jacocoTestReport` depende de `testDebugUnitTest` por lo que ejecutará los tests si no están ya corridos.

3) Abrir el informe HTML (desde cmd.exe):
```bat
start "" "app\build\reports\jacoco\jacocoTestReport\html\index.html"
```

Comandos (Windows - PowerShell)

1) Ejecutar tests unitarios (desde la raíz del repo):
```powershell
& '.\gradlew.bat' ':app:testDebugUnitTest' --no-daemon --stacktrace
```

2) Ejecutar tests + JaCoCo report:
```powershell
& '.\gradlew.bat' ':app:jacocoTestReport' --no-daemon --stacktrace
```

3) Abrir el informe HTML (PowerShell):
```powershell
Start-Process "app\build\reports\jacoco\jacocoTestReport\html\index.html"
```

Detalles y rutas
- Reporte JaCoCo (HTML): `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- Reporte JaCoCo (XML): `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
- Reporte de tests unitarios (HTML): `app/build/reports/tests/testDebugUnitTest/index.html`

Verificación de cobertura (opcional)
- Si quieres hacer que la verificación de cobertura sea obligatoria, ejecuta `gradlew.bat check` (la tarea `check` está enlazada a `jacocoTestCoverageVerification` en el `build.gradle.kts` del módulo `app`). Si la cobertura está por debajo del umbral configurado, la tarea fallará y verás un mensaje con las métricas.

Solución de problemas rápida
- Si `:app:jacocoTestReport` falla por falta de datos de ejecución (`.exec`), asegúrate de que `testDebugUnitTest` se ejecutó y que el directorio `app/build` contiene los archivos mencionados en la configuración de JaCoCo (ej. `jacoco/testDebugUnitTest.exec`).
- Si usas Java 17+ y ves errores relacionados con módulos/access, intenta ejecutar Gradle con las opciones de JVM recomendadas o baja `jvmTarget` temporalmente para los tests (sólo si es necesario).

Ejemplo completo (cmd.exe) — ejecutar tests y abrir reporte automáticamente (ejecutar desde la raíz del repo):
```bat
gradlew.bat :app:jacocoTestReport --no-daemon --stacktrace && start "" "app\build\reports\jacoco\jacocoTestReport\html\index.html"
```

Si quieres, puedo ejecutar ahora la tarea `:app:jacocoTestReport` en tu workspace para generar el informe y confirmar su ubicación; también puedo añadir un pequeño script helper (`scripts\coverage.cmd`) para automatizar esto. ¿Lo ejecuto y te muestro el resultado?

Ejecutar pruebas y ver cobertura (local)
- Ejecutar las pruebas unitarias (JUnit4):
```bat
gradlew.bat testDebugUnitTest --no-daemon --stacktrace
```
- Generar reporte JaCoCo (XML + HTML):
```bat
gradlew.bat testDebugUnitTest jacocoTestReport --no-daemon --stacktrace
# luego abre el HTML
start "" "app\build\reports\jacoco\jacocoTestReport\html\index.html"
```
- Nota: en este proyecto la tarea `jacocoTestCoverageVerification` puede estar enlazada a `check` y provocar que el build falle si la cobertura es menor al umbral configurado (p. ej. 80%).

Archivos importantes
- APK generado: `app\build\outputs\apk\debug\app-debug.apk`
- Test report: `app\build\reports\tests\testDebugUnitTest\index.html`
- JaCoCo report: `app\build\reports\jacoco\jacocoTestReport\html\index.html`

Consejos adicionales
- Si usas Android Studio: después de cambiar versiones de Gradle o AGP, haz "File → Sync Project with Gradle Files" y reconstruye (Build → Rebuild Project).
- Si el emulador no aparece en `adb devices`, reinicia ADB: `adb kill-server` y `adb start-server`.

Scripts helper (ejecutar desde la raíz del repo)

He añadido scripts auxiliares para automatizar build → install → run y para arrancar un AVD si no tienes uno corriendo. Ejecuta los scripts desde la raíz del proyecto. A continuación se muestran los scripts, cómo ejecutarlos y qué hacen.

1) `run-local.cmd` (Windows cmd) — Ejecutar desde la raíz del repo:
```bat
run-local.cmd
```
Qué hace:
- Ejecuta `gradlew.bat clean :app:assembleDebug`.
- Si la build falla, aborta y muestra un mensaje.
- Ejecuta `gradlew.bat :app:installDebug` para instalar el APK en el dispositivo/emulador conectado.
- Lista dispositivos con `adb devices` y arranca la MainActivity con `adb shell am start -n com.team3.vinyls/.MainActivity`.

2) `run-local.ps1` (PowerShell) — Ejecutar desde la raíz del repo (usa ExecutionPolicy Bypass si es necesario):
```powershell
.\run-local.ps1
# o si la política lo impide:
powershell -ExecutionPolicy Bypass -File .\run-local.ps1
```
Qué hace:
- Igual que `run-local.cmd` pero con control de `$LASTEXITCODE` en PowerShell.
- Aborta si build o install fallan.

3) `run-local.sh` (bash / macOS / Linux / WSL) — Ejecutar desde la raíz del repo:
```bash
chmod +x run-local.sh
./run-local.sh
```
Qué hace:
- Ejecuta `./gradlew clean :app:assembleDebug` y `./gradlew :app:installDebug`.
- Muestra dispositivos y arranca la MainActivity.
- El script usa `set -euo pipefail` para abortar en el primer fallo.

4) `run-local-start-avd.ps1` (PowerShell) — inicia un AVD y luego build→install→run (ejecutar desde la raíz del repo):
```powershell
.\run-local-start-avd.ps1
# o arrancar un AVD específico
.\run-local-start-avd.ps1 -AvdName "Pixel_6_API_36"
```
Qué hace:
- Localiza `ANDROID_SDK_ROOT` (o usa `%LOCALAPPDATA%\Android\Sdk`) y verifica `emulator.exe`.
- Lista AVDs (`emulator -list-avds`) y arranca el AVD seleccionado en segundo plano.
- Espera a que `adb` detecte el emulador (`adb wait-for-device`).
- Ejecuta la build (`gradlew.bat clean :app:assembleDebug`), instala (`:app:installDebug`) y arranca la MainActivity.

Notas sobre el emulador y problemas comunes
- Si no hay AVDs: abre Android Studio → AVD Manager y crea uno.
- Si `emulator.exe` no se encuentra: asegúrate de que `ANDROID_SDK_ROOT` o `ANDROID_HOME` apunten al SDK correcto (ejemplo: `C:\Users\USER\AppData\Local\Android\Sdk`).
- Si el emulador tarda en arrancar, `adb` puede tardar en mostrar el dispositivo; usa `adb -s emulator-5554 shell getprop sys.boot_completed` y espera a que devuelva `1`.
- Si la ventana del emulador no aparece o falla, ejecuta el emulador en modo verbose desde la línea de comandos para ver errores:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd "Pixel_6_API_36" -verbose
```

Contacto / ayuda
- Si el script muestra errores, pega aquí la salida completa (la primera excepción/stacktrace o el log del emulador) y lo analizo.

Licencia y notas
- Proyecto educativo/demostración. Ajusta dependencias y configuraciones para producción.

---

Actualizado para facilitar la ejecución local en Windows (PowerShell/cmd). Si quieres lo adapto también con secciones específicas para macOS/Linux (bash) o añado scripts helper (PowerShell .ps1 / batch .cmd) para automatizar el flujo de build → install → run.

## Guía rápida (paso a paso)
A continuación tienes los comandos exactos que puedes copiar/pegar para arrancar el emulador, compilar la app, instalarla y lanzarla — con las variantes para PowerShell y cmd.exe.

1) Arrancar el emulador (PowerShell)
- Si ya sabes el AVD que quieres usar (por ejemplo `Medium_Phone_API_35`), este comando lo arrancará en modo verbose y mostrará logs en la consola:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd "Medium_Phone_API_35" -verbose
```
- Espera a que la interfaz del emulador aparezca y/o a que `adb devices` muestre el emulador.

2) Verificar que el emulador está listo
```powershell
adb devices
# si aparece emulator-5554 (u otro id), comprobar:
adb -s emulator-5554 shell getprop sys.boot_completed
# debe devolver "1" cuando el sistema ha terminado de arrancar
```

3) Compilar, instalar y ejecutar la app (PowerShell) — ejecutar desde la raíz del repo:
```powershell
& '.\gradlew.bat' 'clean' ':app:assembleDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) { Write-Error 'BUILD FAILED'; exit $LASTEXITCODE }
& '.\gradlew.bat' ':app:installDebug' --no-daemon --stacktrace
if ($LASTEXITCODE -ne 0) { Write-Error 'INSTALL FAILED'; exit $LASTEXITCODE }
adb shell am start -n com.team3.vinyls/.MainActivity
```

4) Compilar, instalar y ejecutar la app (cmd.exe) — ejecutar desde la raíz del repo:
```bat
gradlew.bat clean :app:assembleDebug --no-daemon --stacktrace && gradlew.bat :app:installDebug --no-daemon --stacktrace && adb shell am start -n com.team3.vinyls/.MainActivity
```

5) Uso de scripts helper (rápido) — ejecutar desde la raíz del repo
- PowerShell (arranca AVD y ejecuta todo):
```powershell
powershell -ExecutionPolicy Bypass -File .\run-local-start-avd.ps1 -AvdName "Medium_Phone_API_35"
```
- PowerShell (solo build/install/run si el emulador ya está arriba):
```powershell
powershell -ExecutionPolicy Bypass -File .\run-local.ps1
```
- cmd.exe:
```bat
run-local.cmd
```
- bash/WSL/macOS:
```bash
./run-local.sh
```

6) Si algo falla
- Recoge logs y pégalos aquí: salida del emulador (si usaste `-verbose`), `adb devices`, `adb -s <id> shell getprop sys.boot_completed` y `build_log.txt` (si creaste uno con `--info`).
