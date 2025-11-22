const { Given, When, Then } = require('@cucumber/cucumber')
const assert = require('assert')

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`

// Replace direct destructuring import with a safe require + fallback implementation
let dumpPageSource
try {
  const helpers = require('../support/helpers')
  dumpPageSource = helpers && helpers.dumpPageSource
} catch (err) {
  // fallback implementation in case require fails at runtime
  const fs = require('fs')
  const path = require('path')
  dumpPageSource = async (outPrefix, driver) => {
    try {
      const src = await driver.getPageSource()
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (_) {}
      const outPath = path.join(outDir, `${outPrefix}-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (_) {}
    } catch (err) {
      // ignore
    }
  }
}

When('I tap Artist menu', async function () {
  const nav = await this.driver.$(byResId('nav_artists'))

  await nav.click()
  const list = await this.driver.$(byResId('recyclerMusicians'))

  try {
    await list.waitForExist({ timeout: 20000 })
  } catch (e) {
    await dumpPageSource('musicians-list-missing-after-nav', this.driver)
    throw e
  }
})

When('I tap the first artist in the list', async function () {
  const list = await this.driver.$(byResId('recyclerMusicians'))
  await list.waitForExist({ timeout: 20000 })

  const items = await list.$$('*')

  if (!items || items.length === 0) {
    throw new Error('No artists found in recyclerMusicians')
  }

  const firstItem = items[0]

  try {
    const titleInItem = await firstItem.$(byResId('txtMusicianName'))
    if (titleInItem && await titleInItem.isExisting()) {
      await titleInItem.click()
      return
    }
  } catch (_) {
  }

  await firstItem.click()
})


Then('I should see the artist name {string}', async function (expectedName) {
  const byText = (txt) => `//*[contains(@text, "${txt}")]`

  let el = await this.driver.$(byText(expectedName))
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
      const outPath = path.join(outDir, `musicianname-error-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (_) {}
    } catch (_) {}

    throw new Error(`Artist name "${expectedName}" not found`)
  }
})

Then('I should see the artist description', async function () {
  const el = await this.driver.$(byResId('txtArtistDescription'))
  await el.waitForExist({ timeout: 15000 })

  const text = await el.getText()
  assert.ok(text && text.trim().length > 0, 'Artist description is empty')
})

Then('I should see the artist albums section', async function () {
  // scroll to the albums title and assert that the title is visible (more robust)
  await this.driver.$(
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtAlbumsTitle"))'
  )

  const titleEl = await this.driver.$(byResId('txtAlbumsTitle'))
  const titleExists = await titleEl.isExisting()

  if (!titleExists) {
    await dumpPageSource('albums-title-missing', this.driver)
    throw new Error('Artist albums section not visible (title missing)')
  }

  // optional: check if container exists too (if present, good)
  const container = await this.driver.$(byResId('albumsContainer'))
  const containerExists = await container.isExisting().catch(() => false)

  if (!containerExists) {
    // container not present but title exists -> consider this acceptable (no albums yet)
    console.error('Albums title visible but albumsContainer missing (no albums to show)')
  }

  assert.ok(titleExists, 'Artist albums section not visible')
})

Then('I should see the artist prizes section', async function () {
  await this.driver.$(
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtPrizesTitle"))'
  )

  const titleEl = await this.driver.$(byResId('txtPrizesTitle'))
  const titleExists = await titleEl.isExisting()

  if (!titleExists) {
    await dumpPageSource('prizes-title-missing', this.driver)
    throw new Error('Prizes section not visible (title missing)')
  }

  const container = await this.driver.$(byResId('prizesContainer'))
  const containerExists = await container.isExisting().catch(() => false)

  if (!containerExists) {
    console.error('Prizes title visible but prizesContainer missing (no prizes to show)')
  }

  assert.ok(titleExists, 'Prizes section not visible')
})