const { setWorldConstructor, Before, After, setDefaultTimeout } = require('@cucumber/cucumber')
const { remote } = require('webdriverio')
const { execSync } = require('child_process')
const path = require('path')

// aumentar timeout para creación de sesión (300s)
setDefaultTimeout(300 * 1000)

class VinylsWorld {
  constructor() {
    this.driver = null
  }
}

setWorldConstructor(VinylsWorld)

Before(async function () {
  // Selecciona el flavor de la app que quieres usar en la prueba.
  // Por defecto se usa 'e2e' (mock). Para apuntar al backend real usa: E2E_APP_FLAVOR=prod
  const appFlavor = process.env.E2E_APP_FLAVOR || 'e2e'
  // Si E2E_INSTALL_APP=1 intentaremos instalar el APK correspondiente desde la carpeta build/outputs/apk
  const tryInstall = (process.env.E2E_INSTALL_APP === '1')

  // Decide si arrancar la app con noReset (true = conserva estado). Para pruebas contra prod
  // es más seguro arrancar limpio (noReset = false) a menos que se forcee con E2E_NO_RESET.
  let noReset
  if (typeof process.env.E2E_NO_RESET !== 'undefined') {
    // Si se define, interpretamos '0' como false, cualquier otra cosa como true
    noReset = process.env.E2E_NO_RESET !== '0'
  } else {
    // Por defecto: conservar estado en flavor de testing (e2e), pero limpiar en prod
    noReset = appFlavor !== 'prod'
  }

  if (tryInstall) {
    try {
      // Construir ruta al APK (asume build outputs estándar desde la raíz del proyecto)
      // ejemplo: app/build/outputs/apk/prod/debug/app-prod-debug.apk
      const repoRoot = path.resolve(__dirname, '..', '..')
      // repoRoot ya apunta a la raíz del repo; unir directamente con 'app' desde allí
      const apkPath = path.join(repoRoot, 'app', 'build', 'outputs', 'apk', appFlavor, 'debug')
      // Nombre del apk esperado (puede variar); intentar dos convenciones comunes
      const candidates = [
        `app-${appFlavor}-debug.apk`,
        `${appFlavor}-debug.apk`,
        'app-debug.apk'
      ]
      let apkFound = null
      for (const name of candidates) {
        const full = path.join(apkPath, name)
        try {
          require('fs').accessSync(full)
          apkFound = full
          break
        } catch (e) {
          // sigue buscando
        }
      }
      if (apkFound) {
        console.log(`Installing APK ${apkFound} via adb...`)
        // comando para Windows cmd.exe
        execSync(`adb install -r "${apkFound}"`, { stdio: 'inherit' })
      } else {
        console.warn(`APK not found in ${apkPath}. Skipping automatic install. You can install the desired flavor manually.`)
      }
    } catch (e) {
      console.warn('Error installing APK automatically:', e.message)
    }
  }

  this.driver = await remote({
    hostname: process.env.APPIUM_HOST || '127.0.0.1',
    port: Number(process.env.APPIUM_PORT || 4723),
    // usar /wd/hub en caso de que el servidor acepte este basePath
    path: '/',
    protocol: 'http',
    logLevel: 'debug',
    capabilities: {
      platformName: 'Android',
      'appium:deviceName': process.env.ANDROID_DEVICE_NAME || 'Android Emulator',
      'appium:automationName': 'UiAutomator2',
      'appium:appPackage': 'com.team3.vinyls',
      'appium:appActivity': '.MainActivity',
      'appium:noReset': noReset,
      'appium:newCommandTimeout': 120
    }
  })
  // usar API W3C setTimeout para compatibilidad con Appium v2
  await this.driver.setTimeout({ implicit: 5000 })
})

After(async function () {
  if (this.driver) {
    await this.driver.deleteSession()
    this.driver = null
  }
})
