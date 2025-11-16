const { Given, When, Then } = require('@cucumber/cucumber')
const assert = require('assert')

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`

Given('the app is launched', async function () {
  const el = await this.driver.$(byResId('recyclerAlbums'))
  try {
    await el.waitForExist({ timeout: 45000 })
  } catch (e) {
    let src = '<page source unavailable>'
    try {
      src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `pagesource-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (err) {
      // ignore
    }
    console.error('recyclerAlbums not found after 45000ms. Page source (first 400 chars):\n', String(src).slice(0,400))
    throw e
  }
})

Then('I should see the albums list', async function () {
  const list = await this.driver.$(byResId('recyclerAlbums'))
  const exists = await list.isExisting()
  assert.ok(exists, 'Albums list not visible')
})

When('I tap the first album in the list', async function () {
  const list = await this.driver.$(byResId('recyclerAlbums'))
  const firstItem = await list.$('./*')
  if (firstItem && await firstItem.isExisting()) {
    try {
      const titleInItem = await firstItem.$('.//*[@resource-id="com.team3.vinyls:id/txtTitle"]')
      if (titleInItem && await titleInItem.isExisting()) {
        await titleInItem.click()
        return
      }
    } catch (e) {
    }
    await firstItem.click()
  } else {
    const center = await list.getLocation().then(loc => ({ x: loc.x + 50, y: loc.y + 50 })).catch(() => null)
    if (center) await this.driver.touchAction({ action: 'tap', x: center.x, y: center.y })
  }
})

Then('I should see the album name {string}', async function (albumName) {
  const byText = (txt) => `//*[contains(@text, "${txt}")]`

  let el = await this.driver.$(byText(albumName))

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
      const outPath = path.join(outDir, `albumname-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (_) {}
    } catch (_) {}

    console.error(`Album name "${albumName}" not found. Page source (first 400 chars):\n`, String(src).slice(0,400))
    throw new Error(`Album name "${albumName}" not found`)
  }
})

Then('I should see the album description', async function () {
  const el = await this.driver.$(byResId('txtDescription'))
  const exists = await el.isExisting()
  assert.ok(exists, 'Description not visible')
  const text = await el.getText()
  assert.ok(text && text.trim().length > 0, 'Description is empty')
})

Then('I should see the album tracks section', async function () {
  const el = await this.driver.$(byResId('tracksContainer'))
  try {
    await el.waitForExist({ timeout: 15000 })
  } catch (e) {
    const alt = await this.driver.$$('//*[contains(@resource-id, "tracks")]')
    if (alt && alt.length > 0) return


    try {
      try {
        await this.driver.$('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtTracksTitle"))')
        const titleAfter = await this.driver.$(byResId('txtTracksTitle'))
        if (titleAfter && await titleAfter.isExisting()) return
      } catch (scrollErr) {
        try {
          for (let i = 0; i < 3; i++) {
            await this.driver.touchAction([{ action: 'press', x: 540, y: 1600 }, { action: 'moveTo', x: 540, y: 800 }, 'release'])
            await this.driver.pause(500)
            const titleCheck = await this.driver.$(byResId('txtTracksTitle'))
            if (titleCheck && await titleCheck.isExisting()) return
          }
        } catch (swErr) {
        }
      }

      const titleEl = await this.driver.$(byResId('txtTracksTitle'))
      if (titleEl && await titleEl.isExisting()) return
      await titleEl.waitForExist({ timeout: 5000 })
      return
    } catch (err) {
    }

    const texts = ['Canciones', 'Tracks', 'Songs']
    for (const t of texts) {
      try {
        const found = await this.driver.$(`//*[contains(@text, "${t}")]`)
        if (found && await found.isExisting()) return
      } catch (err) {
      }
    }

    try {
      const src = await this.driver.getPageSource()
      const fs = require('fs')
      const path = require('path')
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
      const outPath = path.join(outDir, `tracks-missing-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
    } catch (err) {
    }

    throw new Error('Tracks section not visible')
  }

  const exists = await el.isExisting()
  assert.ok(exists, 'Tracks section not visible')
})
