# Kraken E2E (Appium + Cucumber)

Este directorio contiene la suite E2E basada en Appium + Cucumber para validar las historias HU01 y HU02 (lista de álbumes y detalle).

Este README resume los pasos concretos para ejecutar las pruebas en Windows (PowerShell y cmd.exe) tanto apuntando al backend mock local como al backend real (prod).

Requisitos
- Java / Android SDK / emulador AVD o dispositivo físico con USB debugging.
- Node.js (LTS recomendable) y npm.
- Appium (se usa localmente desde `node_modules`).
- adb en PATH.

Preparación (una sola vez)
1) Instala dependencias Node en `e2e-kraken`:

PowerShell (desde la raíz del repo):
```powershell
Set-Location .\e2e-kraken
npm install
```

cmd.exe (desde la raíz):
```batch
cd e2e-kraken
npm install
```

2) (Appium 2) instala el driver UiAutomator2 si aún no está instalado globalmente:
```bash
# desde cualquier terminal con npx disponible
npx appium driver install uiautomator2
```

3) Asegúrate de haber construido e instalado la app en el emulador/dispositivo
- Para pruebas con el mock (flavor `e2e`):
```powershell
# desde la raíz del repo (PowerShell / cmd ambos válidos)
.\gradlew.bat :app:installE2eDebug
```
- Para pruebas contra el backend real (flavor `prod`):
```powershell
.\gradlew.bat clean :app:assembleProdDebug :app:installProdDebug
```

Nota: si prefieres hacerlo desde Android Studio, construye el flavor correspondiente e instala en el emulador.

Ejecutar Appium
- Desde `e2e-kraken` (manténlo en una terminal separada durante la ejecución de pruebas):
```powershell
# PowerShell
Set-Location .\e2e-kraken
npm run appium
# o desde la raíz (sin cambiar de carpeta):
npm --prefix .\e2e-kraken run appium
```

Ejecutar las pruebas (comandos seguros para PowerShell)

1) Contra el mock local (Express) — asume que `e2e-mock-api` ya está corriendo y que instalaste la app `e2e` en el emulador:
```powershell
Set-Location .\e2e-kraken
# Ejecuta cucumber-js con flavor e2e (por defecto test:android usa el flavor e2e)
npm run test:android
```

2) Contra el backend real (`prod`) — PowerShell (recomendado):
```powershell
Set-Location .\e2e-kraken
$env:E2E_APP_FLAVOR = 'prod'
$env:E2E_INSTALL_APP = '0'   # 0 = no instalar, 1 = intentar instalar antes
# Ejecuta el runner Node que invoca cucumber-js y maneja fallbacks de binarios
node .\scripts\run-test-prod.js
# o usando el script npm
npm run test:android:prod
```

3) Forzar instalación del APK antes de ejecutar (prod):
```powershell
Set-Location .\e2e-kraken
npm run test:android:prod:install
```

Comandos equivalentes para cmd.exe
- Cambia `Set-Location` por `cd` y no uses `$env:...` (usa `set VAR=...` si quieres desde el mismo comando, o usa `npm --prefix` para evitar cambiar carpeta):

Ejemplo cmd.exe, ejecutar test:android:prod usando npm --prefix:
```batch
cd \path\to\repo
set E2E_APP_FLAVOR=prod
set E2E_INSTALL_APP=0
npm --prefix .\e2e-kraken run test:android:prod
```

Notas sobre variables y comportamiento
- `E2E_APP_FLAVOR` controla qué flavor se usa para seleccionar el APK: `e2e` (mock) o `prod` (backend real).
- `E2E_INSTALL_APP`: '1' intenta instalar el APK desde `app/build/outputs/apk/...` antes de crear la sesión; '0' no instala.
- Si `E2E_INSTALL_APP=1`, el hook de `world.js` intentará instalar el APK correspondiente usando `adb install -r`.

Logs y archivos generados
- Los hooks y steps vuelcan artefactos en `e2e-kraken/logs/` cuando algo falla:
  - `tracks-missing-<ts>.xml` → page source cuando la sección de tracks no se encontró.
  - `pagesource-<ts>.xml` → volcado de la pantalla en fases de fallo.
  - `window_dump.xml` → si ejecutas manualmente el dump de `uiautomator`.

Comandos útiles de debugging
```powershell
# Listar dispositivos/emuladores
adb devices

# Limpiar logcat y obtener un nuevo volcado al finalizar la ejecución
adb logcat -c
adb logcat -d > .\e2e-kraken\logs\logcat.txt

# Dump UI y traer al host
adb shell uiautomator dump /sdcard/window_dump.xml
adb pull /sdcard/window_dump.xml .\e2e-kraken\logs\window_dump.xml

# Mostrar los page sources guardados (PowerShell)
Get-ChildItem .\e2e-kraken\logs\*.xml | Sort-Object LastWriteTime -Descending
Get-Content (Get-ChildItem .\e2e-kraken\logs\*.xml | Sort-Object LastWriteTime -Descending | Select-Object -First 1) -TotalCount 500
```

Problemas comunes y soluciones rápidas
- PowerShell no reconoce `&&`: usa `;` o ejecuta comandos por pasos con `Set-Location` y después ejecuta el comando.
- `cross-env` no se encuentra: los scripts usan ahora un runner Node local `scripts/run-test-prod.js` que hace fallbacks y usa `npx` si es necesario.
- `cucumber-js` no se resuelve: asegúrate de haber corrido `npm install` dentro de `e2e-kraken`.
- Si la UI no muestra la sección de tracks: puede deberse a que el elemento esté fuera de vista; la suite ahora intenta hacer `UiScrollable` y varios swipes antes de fallar, y dejará page source en `logs/`.
- Errores de red con el mock (timeouts): verifica que el mock Express esté corriendo en `e2e-mock-api` y que el emulador pueda acceder a `10.0.2.2:3000`.

Contact / notas finales
- Si quieres que automatice la ejecución o suba resultados, dime qué ejecución (mock/prod) quieres que lance y recolecte logs.

---

