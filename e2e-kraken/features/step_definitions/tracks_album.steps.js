const { Given, When, Then } = require('@cucumber/cucumber');
const assert = require('assert');

When('I tap the plus floating button', async function () {
  const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`
  const fab = await this.driver.$(byResId('fabAddTrack'))

  try {
    await fab.waitForExist({ timeout: 15000 })
    await fab.click()
  } catch (e) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `fabAddTrack-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (_) {}

    console.error('fabAddTrack not found or not clickable. Page source (first 400 chars):\n', String(src).slice(0,400))
    throw e
  }
})


When('I tap the add track option', async function () {
  const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`
  const option = await this.driver.$(byResId('option1'))

  try {
    await option.waitForExist({ timeout: 15000 })
    await option.click()
  } catch (e) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `option1-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (_) {}

    console.error('option1 (add track) not found or not clickable. Page source (first 400 chars):\n', String(src).slice(0,400))
    throw e
  }
})


When('I enter the track name {string}', async function (trackName) {
  const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`
  const inputName = await this.driver.$(byResId('inputTrackName'))

  try {
    await inputName.waitForExist({ timeout: 15000 })
    await inputName.setValue(trackName)
  } catch (e) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `inputTrackName-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (_) {}

    console.error('inputTrackName not found or not usable. Page source (first 400 chars):\n', String(src).slice(0,400))
    throw e
  }
})

When('I enter the track duration {string}', async function (duration) {
  const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`
  const inputDuration = await this.driver.$(byResId('inputTrackDuration'))

  try {
    await inputDuration.waitForExist({ timeout: 15000 })
    await inputDuration.setValue(duration)
  } catch (e) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `inputTrackDuration-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (_) {}

    console.error('inputTrackDuration not found or not usable. Page source (first 400 chars):\n', String(src).slice(0,400))
    throw e
  }
})


When('I tap the save track button', async function () {
  const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`
  const saveBtn = await this.driver.$(byResId('btnSave'))

  try {
    await saveBtn.waitForExist({ timeout: 15000 })
    await saveBtn.click()
  } catch (e) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `btnSave-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (_) {}

    console.error('btnSave not found or not clickable. Page source (first 400 chars):\n', String(src).slice(0,400))
    throw e
  }
})

Then('I should see the track added toast', async function () {
  try {
    const toast = await this.driver.$('//android.widget.Toast[contains(@text, "Canción agregada")]')
    await toast.waitForExist({ timeout: 7000 })

        const visible = await toast.isExisting()
        if (!visible) throw new Error('Toast not visible')

      } catch (e) {
        let src = '<page source unavailable>'
        try {
          src = await this.driver.getPageSource()
          const fs = require('fs')
          const path = require('path')
          const ts = new Date().toISOString().replace(/[:.]/g, '-')
          const outDir = path.join(__dirname, '..', '..', 'logs')
          try { fs.mkdirSync(outDir, { recursive: true }) } catch (_) {}
          const outPath = path.join(outDir, `toast-error-${ts}.xml`)
          try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (_) {}
        } catch (_) {}

        console.error('Toast "Canción agregada" not found. Page source (first 400 chars):\n', String(src).slice(0,400))
        throw e
      }
    })

Then('I should see the track name {string} in the tracks list', async function (trackName) {
  const byText = (txt) => `//*[contains(@text, "${txt}")]`

  try {
    await this.driver.$(
      `android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text("${trackName}"))`
    )
  } catch (_) {}

  let el = await this.driver.$(byText(trackName))

  const exists = await el.isExisting()

  if (!exists) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (_) {}
      const outPath = path.join(outDir, `trackname-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (_) {}
    } catch (_) {}

    console.error(`Track name "${trackName}" not found. Page source (first 400 chars):\n`, String(src).slice(0,400))
    throw new Error(`Track name "${trackName}" not found`)
  }
})
