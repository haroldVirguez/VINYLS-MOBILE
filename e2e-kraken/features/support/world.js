const { setWorldConstructor, Before, After, setDefaultTimeout } = require('@cucumber/cucumber')
const { remote } = require('webdriverio')
const { execSync } = require('child_process')
const path = require('path')

setDefaultTimeout(300 * 1000)

class VinylsWorld {
  constructor() {
    this.driver = null
  }
}

setWorldConstructor(VinylsWorld)

Before(async function () {
  // Por defecto se usa 'e2e' (mock). Para apuntar al backend real usa: E2E_APP_FLAVOR=prod
  const appFlavor = process.env.E2E_APP_FLAVOR || 'e2e'
  const tryInstall = (process.env.E2E_INSTALL_APP === '1')

NO_RESET.
  let noReset
  if (typeof process.env.E2E_NO_RESET !== 'undefined') {
    noReset = process.env.E2E_NO_RESET !== '0'
  } else {
    noReset = appFlavor !== 'prod'
  }

  if (tryInstall) {
    try {

      const repoRoot = path.resolve(__dirname, '..', '..')
      const apkPath = path.join(repoRoot, 'app', 'build', 'outputs', 'apk', appFlavor, 'debug')
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
        }
      }
      if (apkFound) {
        console.log(`Installing APK ${apkFound} via adb...`)
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
  await this.driver.setTimeout({ implicit: 5000 })
})

After(async function () {
  if (this.driver) {
    await this.driver.deleteSession()
    this.driver = null
  }
})
