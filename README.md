# Vinyls Mobile

Aplicación Android (Kotlin) con MVVM, Repository y Service Adapter. Presenta una lista de álbumes y una pantalla de detalle.

## Requisitos
- Android Studio (JDK 17 configurado en Gradle)
- Android SDK API 36 (o ajusta `compileSdk`/`targetSdk`)

## Quick Start
1) Abre el proyecto en Android Studio y haz “Sync Project with Gradle Files”.
2) Verifica Gradle JDK=17 (Settings → Build Tools → Gradle).
3) Crea/inicia un emulador en AVD Manager (API 33+ sirve).
4) Ejecuta el módulo `app` desde Android Studio; la app inicia en “Álbumes”.

CLI (opcional, ejecutar desde la raíz del repositorio):

- Unix / Git Bash / macOS / Linux:
```bash
./gradlew clean :app:assembleDebug :app:installDebug
adb shell am start -n com.team3.vinyls/.MainActivity
```

- Windows (cmd.exe / PowerShell, ejecutar desde la raíz del repo):
```powershell
.\gradlew.bat clean :app:assembleDebug :app:installDebug
adb shell am start -n com.team3.vinyls/.MainActivity
```

Nota: no es necesario usar rutas absolutas; asume que estás en la raíz del repositorio antes de ejecutar los comandos.

## Pruebas y cobertura
- Ejecutar pruebas unitarias: `./gradlew test` (o `.
gradlew.bat test` en Windows)
- Reporte JaCoCo: `./gradlew jacocoTestReport` y abre `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- Umbral de cobertura: 80% (la tarea `jacocoTestCoverageVerification` y `check` fallan si no se alcanza)

## Arquitectura (breve)
- UI: `AlbumsFragment`, `AlbumsAdapter`, `AlbumDetailFragment`
- MVVM: `AlbumsViewModel` (inyectable para tests)
- Repository: `AlbumRepository`
- Service Adapter: `AlbumsService` (Retrofit) + `NetworkModule` (OkHttp/Moshi)
- Diagramas (draw.io / SVG): `docs/diagrams/`

## CI
- GitHub Actions: build + tests + cobertura (80%) con reporte HTML como artifact.

## Configuración de red
- `baseUrl` de ejemplo: `https://example.com/api/`. Sustituye por la URL real al conectar backend.
