# Kraken E2E (Appium + Cucumber)

Guía corta para ejecutar las pruebas E2E (HU01/HU02) en Windows.

Prerequisitos
- Java + Android SDK + emulador AVD o dispositivo USB (adb en PATH).
- Node.js (LTS) y npm.
- Haber corrido `npm install` en `e2e-kraken`.

Pasos rápidos (mock local, flavor `e2e`)
1) Instalar dependencias Node (una sola vez)

PowerShell:
```powershell
Set-Location .\e2e-kraken
npm install
```

cmd.exe:
```batch
cd e2e-kraken
npm install
```

2) Instalar y preparar la app en el emulador (desde la raíz del repo):
```powershell
# instalar el flavor e2e (usa el mock API)
.\gradlew.bat :app:installE2eDebug
```

3) Levantar el mock API (en otra terminal, desde la raíz):
```powershell
npm --prefix .\e2e-mock-api start
```

4) Ejecutar Appium (en una terminal separada):
```powershell
npm --prefix .\e2e-kraken run appium
```

5) Ejecutar las pruebas (con Appium corriendo):
```powershell
Set-Location .\e2e-kraken
npm run test:android
```

Comandos equivalentes en cmd.exe (ejemplo):
```batch
cd \path\to\repo
cd e2e-kraken
npm run test:android
```

Notas rápidas
- Logs y page-sources se guardan en `e2e-kraken/logs/` cuando hay fallos.
- Variables útiles:
  - `E2E_APP_FLAVOR`: `e2e` (mock) o `prod` (backend real).
  - `E2E_INSTALL_APP`: `1` intenta instalar APK antes de correr, `0` no.
- Si quieres ejecutar contra `prod`, define `E2E_APP_FLAVOR=prod` y usa el script `npm run test:android:prod`.

Problemas comunes
- Asegúrate de que el emulador esté listado en `adb devices`.
- Si `cucumber-js` no está, corre `npm install` en `e2e-kraken`.

Si quieres, puedo añadir un pequeño script PowerShell que automatice los pasos 2–5 para ejecutar todo con un solo comando.

## Escenarios de prueba E2E con Espresso
- Espresso (`app/src/androidTestE2e`): pruebas instrumentadas en Kotlin, rápidas y estables, ideales para CI y regresiones rápidas.
- Kraken (`e2e-kraken`): Appium + Gherkin para pruebas de aceptación/BDD y soporte multiplataforma (útil para QA/product).

## Ejecutar las pruebas Espresso (rápido)
Desde la raíz del repo, pasos mínimos:

1) Asegúrate de que el emulador esté corriendo y `adb devices` muestre el dispositivo.
2) (Opcional) Instala el flavor e2e si no está instalado:

```powershell
.\gradlew.bat :app:installE2eDebug
```

3) Ejecuta los tests instrumentados:

```powershell
.\gradlew.bat :app:connectedE2eDebugAndroidTest
```

- Ejecuta desde PowerShell en la raíz del proyecto. 
