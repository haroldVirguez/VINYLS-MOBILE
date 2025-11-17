const { setWorldConstructor, Before, After, setDefaultTimeout } = require('@cucumber/cucumber')
const { remote } = require('webdriverio')
const { execSync } = require('child_process')
const path = require('path')
const fs = require('fs')
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

// NO_RESET.
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
      'appium:newCommandTimeout': 120,
      "appium:recordVideo": true
    }
  })
  await this.driver.setTimeout({ implicit: 5000 })

  try {
    try {
      await this.driver.startActivity('com.team3.vinyls', '.MainActivity')
    } catch (e) {
      try { await this.driver.activateApp('com.team3.vinyls') } catch (err) {}
    }

    try {
      const recycler = await this.driver.$('//*[@resource-id="com.team3.vinyls:id/recyclerAlbums"]')
      await recycler.waitForExist({ timeout: 10000 })
    } catch (e) {
      try {
        const nav = await this.driver.$('//*[@resource-id="com.team3.vinyls:id/nav_albums"]')
        if (nav && await nav.isExisting()) {
          await nav.click()
          const recycler2 = await this.driver.$('//*[@resource-id="com.team3.vinyls:id/recyclerAlbums"]')
          await recycler2.waitForExist({ timeout: 10000 })
        }
      } catch (err) {
      }
    }
  } catch (err) {
  }
  if (this.driver?.startRecordingScreen) {
    await this.driver.startRecordingScreen();
    await new Promise(res => setTimeout(res, 500));
  }
})

After(async function (scenario) {
  if (this.driver) {
    if (this.driver.stopRecordingScreen) {
          const videoBase64 = await this.driver.stopRecordingScreen();
          const filename = scenario.pickle.name
            .replace(/[^a-z0-9]/gi, '_')
            .toLowerCase();
    
          const videosDir = path.join(process.cwd(), 'reports', 'videos');
    
          if (!fs.existsSync(videosDir)) {
            fs.mkdirSync(videosDir, { recursive: true });
          }
    
          const filePath = path.join(videosDir, `${filename}.mp4`);
          fs.writeFileSync(filePath, videoBase64, 'base64');
    
          console.log(`🎥 Saved video: ${filePath}`);
        }
    await this.driver.deleteSession()
    this.driver = null
  }
})
