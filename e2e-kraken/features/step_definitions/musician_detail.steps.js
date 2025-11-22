const { Given, When, Then } = require('@cucumber/cucumber')
const assert = require('assert')

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`

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
  await this.driver.$(
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtAlbumsTitle"))'
  )

  const el = await this.driver.$(byResId('albumsContainer'))
  const exists = await el.isExisting()

  if (!exists) {
    await dumpPageSource('albums-missing', this.driver)
    throw new Error('Artist albums section not visible')
  }

  assert.ok(exists, 'Artist albums section not visible')
})

Then('I should see the artist prizes section', async function () {
  await this.driver.$(
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtPrizesTitle"))'
  )

  const el = await this.driver.$(byResId('prizesContainer'))
  const exists = await el.isExisting()

  if (!exists) {
    await dumpPageSource('prizes-missing', this.driver)
    throw new Error('Prizes section not visible')
  }

  assert.ok(exists, 'Prizes section not visible')
})