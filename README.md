# Spinify Mobile

Aplicación Android (Kotlin) con MVVM, Repository y Service Adapter. Presenta una lista de álbumes y una pantalla de detalle.

## Requisitos
- Android Studio (JDK 17 configurado en Gradle)
- Android SDK API 36 (o ajusta `compileSdk`/`targetSdk`)

## Quick Start
1) Abre el proyecto en Android Studio y haz "Sync Project with Gradle Files".
2) Verifica Gradle JDK=17 (Settings → Build Tools → Gradle).
3) Crea/inicia un emulador en AVD Manager (API 33+ sirve).
4) Selecciona el flavor de construcción:
   - En Android Studio: **View → Tool Windows → Build Variants** (o pestaña inferior "Build Variants")
   - Para producción: selecciona **prodDebug** (usa backend real `https://backvynils-q6yc.onrender.com/`)
   - Para pruebas E2E: selecciona **e2eDebug** (requiere servidor mock local corriendo)
5) Ejecuta el módulo `app` desde Android Studio; la app inicia en "Álbumes".

CLI (opcional, ejecutar desde la raíz del repositorio):

- Unix / Git Bash / macOS / Linux:
```bash
# Producción
./gradlew clean :app:assembleProdDebug :app:installProdDebug

# E2E (requiere servidor mock)
./gradlew clean :app:assembleE2eDebug :app:installE2eDebug

adb shell am start -n com.team3.vinyls/.MainActivity
```

- Windows (cmd.exe / PowerShell, ejecutar desde la raíz del repo):
```powershell
# Producción
.\gradlew.bat clean :app:assembleProdDebug :app:installProdDebug

# E2E (requiere servidor mock)
.\gradlew.bat clean :app:assembleE2eDebug :app:installE2eDebug

adb shell am start -n com.team3.vinyls/.MainActivity
```

**Nota:** no es necesario usar rutas absolutas; asume que estás en la raíz del repositorio antes de ejecutar los comandos.

## Pruebas y cobertura

**Windows (PowerShell/cmd.exe):**
```powershell
# Ejecutar pruebas unitarias
.\gradlew.bat test

# Reporte JaCoCo
.\gradlew.bat jacocoTestReport
# Luego abre: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

**macOS / Linux / Unix:**
```bash
# Ejecutar pruebas unitarias
./gradlew test

# Reporte JaCoCo
./gradlew jacocoTestReport
# Luego abre: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

- Umbral de cobertura: 80% (la tarea `jacocoTestCoverageVerification` y `check` fallan si no se alcanza)

## E2E con Express (HU01 y HU02)
Esta app incluye un flavor `e2e` que apunta a un backend mock en Express para ejecutar pruebas E2E controladas sobre HU01 (catálogo) y HU02 (detalle).

### 1) Iniciar el backend mock (Express)
Recomendado antes de ejecutar las pruebas E2E: verifica la versión de Node y usa `npm ci` cuando exista `package-lock.json` para instalaciones reproducibles.

```powershell
# Desde la raíz del repo
Set-Location .\e2e-mock-api

# Instala dependencias (si ya existe package-lock.json, preferir npm ci)
npm ci            # instalación reproducible (use en CI y si ya tiene package-lock.json)
# o, si es la primera vez o no hay package-lock.json:
# npm install

# Inicia el servidor mock
npm start
# El mock escuchará en http://localhost:3000
```

Notas de red:
- Emulador Android (AVD): la URL `http://10.0.2.2:3000/` apunta al localhost de tu máquina. Ya está configurada en el flavor `e2e`.
- Dispositivo físico: ejecuta `adb reverse tcp:3000 tcp:3000` y (opcional) cambia la URL del flavor `e2e` a `http://127.0.0.1:3000/` en `app/build.gradle.kts` si no usas emulador.

### 2) Ejecutar pruebas E2E (Espresso)
```bash
./gradlew connectedE2eDebugAndroidTest
```

La suite principal vive en:
- `app/src/androidTestE2e/java/com/team3/vinyls/e2e/E2EAlbumsFlowTest.kt`

Qué valida:
- HU01: lista de álbumes se renderiza desde `/albums` del mock.
- HU02: al tocar un álbum, se navega a detalle y se muestra el título correcto.

### 3) Cambiar entre ambientes

#### En Android Studio:
1. Abre **View → Tool Windows → Build Variants** (o pestaña inferior "Build Variants")
2. En la columna "Active Build Variant" para el módulo `app`, selecciona:
   - **prodDebug** para producción (backend real)
   - **e2eDebug** para pruebas (mock local)

#### Desde línea de comandos:

**Windows (PowerShell/cmd.exe):**
```powershell
# Producción (backend real)
.\gradlew.bat :app:installProdDebug

# E2E (mock local - requiere servidor corriendo)
.\gradlew.bat :app:installE2eDebug
```

**macOS / Linux / Unix:**
```bash
# Producción (backend real)
./gradlew :app:installProdDebug

# E2E (mock local - requiere servidor corriendo)
./gradlew :app:installE2eDebug
```

**Configuración:**
- `prod`: usa el backend real `https://backvynils-q6yc.onrender.com/`
- `e2e`: usa el mock `http://10.0.2.2:3000/` (emulador). Configurado vía `BuildConfig.BASE_URL` en `app/build.gradle.kts`

Archivos clave:
- `app/build.gradle.kts` → flavors con `BuildConfig.BASE_URL`.
- `app/src/main/java/com/team3/vinyls/core/network/ApiConstants.kt` → toma `BASE_URL` de `BuildConfig`.
- `e2e-mock-api/server.js` → Express con `/albums` y `/albums/:id`.

## Kraken (Appium + Cucumber) — e2e-kraken
Se provee una suite alternativa de e2e basada en Appium + Cucumber (ubicada en `e2e-kraken/`).

- `e2e-kraken/package.json` contiene ahora scripts útiles:
  - `npm run appium` → arranca appium (usa `appium` instalado localmente)
  - `npm run test:android` → corre `cucumber-js` (por defecto usa el flavor `e2e`)
  - `npm run test:android:prod` → corre las pruebas apuntando a `prod` (usa un runner Node local que establece las variables de entorno)
  - `npm run test:android:prod:install` → como anterior pero fuerza la instalación del APK antes de ejecutar

Resumen de pasos para Kraken

1) Construir e instalar la app (flavor mock/e2e) y/o prod según necesites:

**Windows (PowerShell/cmd.exe):**
```powershell
# instalar flavor e2e (mock)
.\gradlew.bat :app:installE2eDebug

# instalar flavor prod (backend real)
.\gradlew.bat clean :app:assembleProdDebug :app:installProdDebug
```

**macOS / Linux / Unix:**
```bash
# instalar flavor e2e (mock)
./gradlew :app:installE2eDebug

# instalar flavor prod (backend real)
./gradlew clean :app:assembleProdDebug :app:installProdDebug
```
2) (Opcional) Iniciar Appium desde `e2e-kraken`:

**Windows (PowerShell):**
```powershell
Set-Location .\e2e-kraken
npm run appium
# o desde la raíz:
npm --prefix .\e2e-kraken run appium
```

**macOS / Linux / Unix:**
```bash
cd e2e-kraken
npm run appium
# o desde la raíz:
npm --prefix ./e2e-kraken run appium
```

3) Ejecutar las pruebas

- Contra el mock (flavor `e2e`, asumiendo el server Express corriendo):

**Windows (PowerShell):**
```powershell
Set-Location .\e2e-kraken
npm run test:android
```

**macOS / Linux / Unix:**
```bash
cd e2e-kraken
npm run test:android
```

- Contra el backend real (`prod`):

**Windows (PowerShell):**
```powershell
Set-Location .\e2e-kraken
$env:E2E_APP_FLAVOR = 'prod'
$env:E2E_INSTALL_APP = '0'
node .\scripts\run-test-prod.js
# o alternativamente
npm run test:android:prod
```

**macOS / Linux / Unix:**
```bash
cd e2e-kraken
export E2E_APP_FLAVOR=prod
export E2E_INSTALL_APP=0
node ./scripts/run-test-prod.js
# o alternativamente
npm run test:android:prod
```

- Para forzar instalación del APK antes de ejecutar (prod):

**Windows (PowerShell):**
```powershell
Set-Location .\e2e-kraken
npm run test:android:prod:install
```

**macOS / Linux / Unix:**
```bash
cd e2e-kraken
npm run test:android:prod:install
```

Logs y debugging
- Los hooks y las steps vuelcan page sources y logs en `e2e-kraken/logs/` cuando algo falla (p.ej. `tracks-missing-<ts>.xml`).
- Para capturar un dump UI manualmente:
```powershell
adb shell uiautomator dump /sdcard/window_dump.xml
adb pull /sdcard/window_dump.xml .\e2e-kraken\logs\window_dump.xml
```

Notas importantes
- **Windows/PowerShell:** PowerShell puede interpretar `&&` de forma distinta en versiones antiguas. Para comandos encadenados usa `;` o ejecuta por pasos (Set-Location; luego ejecutar). También puedes usar `npm --prefix` para ejecutar scripts desde la raíz sin cambiar de carpeta.
- **macOS/Linux:** Los comandos `adb` funcionan igual en todos los sistemas operativos.
- Si ves errores de resolución de binarios (`cross-env`), los scripts ahora usan un runner Node local (`scripts/run-test-prod.js`) que maneja fallbacks y `npx` cuando sea necesario.

Para más detalles y ejemplos de ejecución (comandos listos para copiar/pegar), abre `e2e-kraken/README.md`.

## Arquitectura (breve)
- UI: `AlbumsFragment`, `AlbumsAdapter`, `AlbumDetailFragment`
- MVVM: `AlbumsViewModel` (inyectable para tests)
- Repository: `AlbumRepository`
- Service Adapter: `AlbumsService` (Retrofit) + `NetworkModule` (OkHttp/Moshi)
- Diagramas (draw.io / SVG): `docs/diagrams/`

## CI
- GitHub Actions: build + tests + cobertura (80%) con reporte HTML como artifact.

## Troubleshooting (mock Express)
Si al iniciar el server mock (`npm start`) obtienes errores o comportamientos inesperados, aquí hay soluciones rápidas para los más comunes.

- Error: "listen EADDRINUSE: address already in use :::3000"
  - Significa que otro proceso ya está escuchando en el puerto 3000.
  - Windows (cmd.exe / PowerShell):

```powershell
# Ver PID del proceso que usa el puerto 3000
netstat -ano | findstr ":3000"
# Mata el proceso (reemplaza <PID> con el número mostrado)
taskkill /PID <PID> /F
```

  - Unix / macOS / Git Bash:

```bash
# Ver PID
lsof -i :3000
# Mata el proceso
kill -9 <PID>
# Alternativa moderna (instala kill-port si lo prefieres)
npx kill-port 3000
```

  - Alternativa: arrancar el mock en otro puerto (por ejemplo 3001):

```bash
# Unix
PORT=3001 npm start
# Windows PowerShell
$env:PORT=3001; npm start
# Windows cmd.exe
set PORT=3001 && npm start
```

- Error al ejecutar comandos que contienen texto del README o prompts (por ejemplo `host:3000#`)
  - Asegúrate de copiar sólo las líneas de comando (no el prompt ni anotaciones). Por ejemplo, ejecuta:

```bash
cd e2e-mock-api
npm ci    # o npm install
npm start
```

  - No pegues comentarios o salidas de consola en la terminal; eso genera errores tipo "CommandNotFoundException" en PowerShell.

- Node / versiones incompatibles
  - Comprueba la versión de Node:

```bash
node -v
```

  - Recomendado: usar una versión LTS (por ejemplo Node 18.x o 20.x). Si tienes nvm, cambia con `nvm use <version>`.

- `npm install` y vulnerabilidades (`npm audit`)
  - Siempre revisa con `npm audit` y aplica `npm audit fix` si es posible.
  - No uses `npm audit fix --force` sin revisar: puede introducir breaking changes. En su lugar, prueba actualizar paquetes concretos o usar `npx npm-check-updates` para actualizar controladamente.

- Rutas duplicadas o errores "No se encuentra la ruta" al usar `cd`
  - Asegúrate de ejecutar `cd e2e-mock-api` desde la raíz del repo (`C:\Users\USER\AndroidStudioProjects\VINYLS-MOBILE`), no desde dentro de `e2e-mock-api` otra vez.

- Logs y depuración
  - Si el servidor falla al iniciar, revisa `server.js` (línea indicada en la traza) y mira la salida completa de `npm start`.
```
