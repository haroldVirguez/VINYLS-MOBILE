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

Then('I should see the album detail title {string}', async function (expectedTitle) {
  const titleEl = await this.driver.$(byResId('txtTitle'))
  await titleEl.waitForExist({ timeout: 10000 })
  const text = await titleEl.getText()
  // Si estamos ejecutando contra el backend real (prod), no asumimos valores mock concretos.
  if (process.env.E2E_APP_FLAVOR === 'prod') {
    assert.ok(text && text.trim().length > 0, 'Title is empty')
  } else {
    assert.strictEqual(text, expectedTitle, `Unexpected title: ${text}`)
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
    // esperar un poco más por la carga asíncrona
    await el.waitForExist({ timeout: 15000 })
  } catch (e) {
    // fallback 1: buscar cualquier elemento con 'tracks' en el resource-id
    const alt = await this.driver.$$('//*[contains(@resource-id, "tracks")]')
    if (alt && alt.length > 0) return

    // fallback 2: comprobar título de sección (txtTracksTitle)
    try {
      // intento de scroll usando UiScrollable hacia el título de tracks
      try {
        await this.driver.$('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtTracksTitle"))')
        // si no lanza excepción, asumimos que ahora está visible
        const titleAfter = await this.driver.$(byResId('txtTracksTitle'))
        if (titleAfter && await titleAfter.isExisting()) return
      } catch (scrollErr) {
        // fallback swipe up un par de veces
        try {
          for (let i = 0; i < 3; i++) {
            // coordenadas aproximadas; si fallan, no bloquear
            await this.driver.touchAction([{ action: 'press', x: 540, y: 1600 }, { action: 'moveTo', x: 540, y: 800 }, 'release'])
            await this.driver.pause(500)
            const titleCheck = await this.driver.$(byResId('txtTracksTitle'))
            if (titleCheck && await titleCheck.isExisting()) return
          }
        } catch (swErr) {
          // ignore
        }
      }

      const titleEl = await this.driver.$(byResId('txtTracksTitle'))
      if (titleEl && await titleEl.isExisting()) return
      // intentar esperar un poco por si aparece
      await titleEl.waitForExist({ timeout: 5000 })
      return
    } catch (err) {
      // ignore
    }

    // fallback 3: buscar textos localizados comunes (es/eng)
    const texts = ['Canciones', 'Tracks', 'Songs']
    for (const t of texts) {
      try {
        const found = await this.driver.$(`//*[contains(@text, "${t}")]`)
        if (found && await found.isExisting()) return
      } catch (err) {
        // ignore
      }
    }

    // volcar page source para investigar por qué falta
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
      // ignore
    }

    throw new Error('Tracks section not visible')
  }

  const exists = await el.isExisting()
  assert.ok(exists, 'Tracks section not visible')
})
